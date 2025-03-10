/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.spark.sql.sedona_sql.expressions

import org.apache.sedona.common.Functions
import org.apache.sedona.sql.utils.GeometrySerializer
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.catalyst.expressions.{Expression, Generator}
import org.apache.spark.sql.catalyst.util.{ArrayData, GenericArrayData}
import org.apache.spark.sql.sedona_sql.UDT.GeometryUDT
import org.apache.spark.sql.sedona_sql.expressions.collect.Collect
import org.apache.spark.sql.sedona_sql.expressions.implicits._
import org.apache.spark.sql.sedona_sql.expressions.subdivide.GeometrySubDivider
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String
import org.locationtech.jts.algorithm.MinimumBoundingCircle
import org.locationtech.jts.geom.util.GeometryFixer
import org.locationtech.jts.geom._
import org.locationtech.jts.linearref.LengthIndexedLine
import org.locationtech.jts.operation.buffer.BufferParameters
import org.locationtech.jts.operation.linemerge.LineMerger
import org.locationtech.jts.precision.GeometryPrecisionReducer
import org.locationtech.jts.simplify.TopologyPreservingSimplifier

/**
  * Return the distance between two geometries.
  *
  * @param inputExpressions This function takes two geometries and calculates the distance between two objects.
  */
case class ST_Distance(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.distance) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_YMax(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.yMax) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_YMin(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.yMin) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_3DDistance(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.distance3d) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Return the convex hull of a Geometry.
  *
  * @param inputExpressions
  */
case class ST_ConvexHull(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override def nullSafeEval(geometry: Geometry): Any = {
    new GenericArrayData(GeometrySerializer.serialize(geometry.convexHull()))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Return the number of Points in geometry.
  *
  * @param inputExpressions
  */
case class ST_NPoints(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.nPoints) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Returns a geometry/geography that represents all points whose distance from this Geometry/geography is less than or equal to distance.
  *
  * @param inputExpressions
  */
case class ST_Buffer(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.buffer) with CodegenFallback {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


/**
  * Return the bounding rectangle for a Geometry
  *
  * @param inputExpressions
  */
case class ST_Envelope(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.envelope) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Return the length measurement of a Geometry
  *
  * @param inputExpressions
  */
case class ST_Length(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.length) with CodegenFallback {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Return the area measurement of a Geometry.
  *
  * @param inputExpressions
  */
case class ST_Area(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.area) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Return mathematical centroid of a geometry.
  *
  * @param inputExpressions
  */
case class ST_Centroid(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override def nullSafeEval(geometry: Geometry): Any = {
    new GenericArrayData(GeometrySerializer.serialize(geometry.getCentroid()))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Given a geometry, sourceEPSGcode, and targetEPSGcode, convert the geometry's Spatial Reference System / Coordinate Reference System.
  *
  * @param inputExpressions
  */
case class ST_Transform(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  assert(inputExpressions.length >= 3 && inputExpressions.length <= 4)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val geometry = inputExpressions(0).toGeometry(input)
    val sourceCRS = inputExpressions(1).asString(input)
    val targetCRS = inputExpressions(2).asString(input)

    (geometry, sourceCRS, targetCRS) match {
      case (geometry: Geometry, sourceCRS: String, targetCRS: String) =>
        val lenient = if (inputExpressions.length == 4) {
          inputExpressions(3).eval(input).asInstanceOf[Boolean]
        } else {
          false
        }
        Functions.transform(geometry, sourceCRS, targetCRS, lenient).toGenericArrayData
      case (_, _, _) => null
    }
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


/**
  * Return the intersection shape of two geometries. The return type is a geometry
  *
  * @param inputExpressions
  */
case class ST_Intersection(inputExpressions: Seq[Expression])
  extends BinaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 2)

  lazy val GeometryFactory = new GeometryFactory()
  lazy val emptyPolygon = GeometryFactory.createPolygon(null, null)

  override def nullSafeEval(leftGeometry: Geometry, rightGeometry: Geometry): Any = {
    val isIntersects = leftGeometry.intersects(rightGeometry)
    lazy val isLeftContainsRight = leftGeometry.contains(rightGeometry)
    lazy val isRightContainsLeft = rightGeometry.contains(leftGeometry)

    if (!isIntersects) {
      return new GenericArrayData(GeometrySerializer.serialize(emptyPolygon))
    }

    if (isIntersects && isLeftContainsRight) {
      return new GenericArrayData(GeometrySerializer.serialize(rightGeometry))
    }

    if (isIntersects && isRightContainsLeft) {
      return new GenericArrayData(GeometrySerializer.serialize(leftGeometry))
    }

    new GenericArrayData(GeometrySerializer.serialize(leftGeometry.intersection(rightGeometry)))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Given an invalid geometry, create a valid representation of the geometry.
  * See: http://lin-ear-th-inking.blogspot.com/2021/05/fixing-invalid-geometry-with-jts.html
  *
  * @param inputExpressions
  */
case class ST_MakeValid(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  assert(inputExpressions.length == 1 || inputExpressions.length == 2)

  override def eval(input: InternalRow): Any = {
    val geometry = inputExpressions.head.toGeometry(input)
    val keepCollapsed = if (inputExpressions.length == 2) {
      inputExpressions(1).eval(input).asInstanceOf[Boolean]
    } else {
      false
    }
    (geometry) match {
      case (geometry: Geometry) => nullSafeEval(geometry, keepCollapsed)
      case _ => null
    }
  }

  private def nullSafeEval(geometry: Geometry, keepCollapsed: Boolean) = {
    val fixer = new GeometryFixer(geometry)
    fixer.setKeepCollapsed(keepCollapsed)
    fixer.getResult.toGenericArrayData
  }

  override def nullable: Boolean = true

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Test if Geometry is valid.
  *
  * @param inputExpressions
  */
case class ST_IsValid(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.isValid) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Test if Geometry is simple.
  *
  * @param inputExpressions
  */
case class ST_IsSimple(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.isSimple) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Simplifies a geometry and ensures that the result is a valid geometry having the same dimension and number of components as the input,
  * and with the components having the same topological relationship.
  * The simplification uses a maximum-distance difference algorithm similar to the Douglas-Peucker algorithm.
  *
  * @param inputExpressions first arg is geometry
  *                         second arg is distance tolerance for the simplification(all vertices in the simplified geometry will be within this distance of the original geometry)
  */
case class ST_SimplifyPreserveTopology(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  assert(inputExpressions.length == 2)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val distanceTolerance = inputExpressions(1).eval(input) match {
      case number: Decimal => number.toDouble
      case number: Double => number
      case number: Int => number.toDouble
    }
    inputExpressions(0).toGeometry(input) match {
      case geometry: Geometry => TopologyPreservingSimplifier.simplify(geometry, distanceTolerance).toGenericArrayData
      case _ => null
    }
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Reduce the precision of the given geometry to the given number of decimal places
  *
  * @param inputExpressions The first arg is a geom and the second arg is an integer scale, specifying the number of decimal places of the new coordinate. The last decimal place will
  *                         be rounded to the nearest number.
  */
case class ST_PrecisionReduce(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val precisionScale = inputExpressions(1).eval(input).asInstanceOf[Int]
    inputExpressions(0).toGeometry(input) match {
      case geometry: Geometry =>
        val precisionReduce =new GeometryPrecisionReducer(new PrecisionModel(Math.pow(10, precisionScale)))
        precisionReduce.reduce(geometry).toGenericArrayData
      case _ => null
    }
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AsText(inputExpressions: Seq[Expression])
  extends  InferredUnaryExpression(Functions.asEWKT) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AsGeoJSON(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.asGeoJson) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AsBinary(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.asEWKB) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AsEWKB(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.asEWKB) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_SRID(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.getSRID) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_SetSRID(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.setSRID) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_GeometryType(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override protected def nullSafeEval(geometry: Geometry): Any = {
    UTF8String.fromString("ST_" + geometry.getGeometryType)
  }

  override def dataType: DataType = StringType

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Returns a LineString formed by sewing together the constituent line work of a MULTILINESTRING.
  * Only works for MultiLineString. Using other geometry will return GEOMETRYCOLLECTION EMPTY
  * If the MultiLineString is can't be merged, the original multilinestring is returned
  *
  * @param inputExpressions Geometry
  */
case class ST_LineMerge(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  // Definition of the Geometry Collection Empty
  lazy val GeometryFactory = new GeometryFactory()
  lazy val emptyGeometry = GeometryFactory.createGeometryCollection(null)

  override protected def nullSafeEval(geometry: Geometry): Any = {
    val merger = new LineMerger()

    val output: Geometry = geometry match {
      case g: MultiLineString => {
        // Add the components of the multilinestring to the merger
        (0 until g.getNumGeometries).map(i => {
          val line = g.getGeometryN(i).asInstanceOf[LineString]
          merger.add(line)
        })
        if (merger.getMergedLineStrings().size() == 1) {
          // If the merger was able to join the lines, there will be only one element
          merger.getMergedLineStrings().iterator().next().asInstanceOf[Geometry]
        } else {
          // if the merger couldn't join the lines, it will contain the individual lines, so return the input
          geometry
        }
      }
      case _ => emptyGeometry
    }
    new GenericArrayData(GeometrySerializer.serialize(output))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_Azimuth(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.azimuth) with CodegenFallback {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_X(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.x) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_Y(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.y) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_Z(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.z) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_StartPoint(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override protected def nullSafeEval(geometry: Geometry): Any = {
    geometry match {
      case line: LineString => line.getPointN(0).toGenericArrayData
      case _ => null
    }
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_Boundary(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.boundary) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_MinimumBoundingRadius(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  private val geometryFactory = new GeometryFactory()

  override protected def nullSafeEval(geometry: Geometry): Any = {
    getMinimumBoundingRadius(geometry)
  }

  private def getMinimumBoundingRadius(geom: Geometry): InternalRow = {
    val minimumBoundingCircle = new MinimumBoundingCircle(geom)
    val centerPoint = geometryFactory.createPoint(minimumBoundingCircle.getCentre)
    InternalRow(centerPoint.toGenericArrayData, minimumBoundingCircle.getRadius)
  }

  override def dataType: DataType = DataTypes.createStructType(
    Array(
      DataTypes.createStructField("center", GeometryUDT, false),
      DataTypes.createStructField("radius", DataTypes.DoubleType, false)
    )
  )

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_MinimumBoundingCircle(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  inputExpressions.betweenLength(1, 2)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val geometry = inputExpressions.head.toGeometry(input)
    val quadrantSegments = if (inputExpressions.length == 2) {
      inputExpressions(1).toInt(input)
    } else {
      BufferParameters.DEFAULT_QUADRANT_SEGMENTS
    }
    geometry match {
      case geom: Geometry => getMinimumBoundingCircle(geom, quadrantSegments).toGenericArrayData
      case _ => null
    }
  }

  private def getMinimumBoundingCircle(geom: Geometry, quadrantSegments: Int): Geometry = {
    val minimumBoundingCircle = new MinimumBoundingCircle(geom)
    val centre = minimumBoundingCircle.getCentre
    val radius = minimumBoundingCircle.getRadius
    var circle: Geometry = null
    if (centre == null) {
      circle = geom.getFactory.createPolygon
    } else {
      circle = geom.getFactory.createPoint(centre)
      if (radius != 0D)
        circle = circle.buffer(radius, quadrantSegments)
    }
    circle
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


/**
 * Return a linestring being a substring of the input one starting and ending at the given fractions of total 2d length.
 * Second and third arguments are Double values between 0 and 1. This only works with LINESTRINGs.
 *
 * @param inputExpressions
 */
case class ST_LineSubstring(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  assert(inputExpressions.length == 3)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val geometry = inputExpressions.head.toGeometry(input)
    val fractions = inputExpressions.slice(1, 3).map{
      x => x.eval(input) match {
        case a: Decimal => a.toDouble
        case a: Double => a
        case a: Int => a
      }
    }

    (geometry, fractions) match {
      case (g:LineString, r:Seq[Double]) if r.head >= 0 && r.last <= 1 && r.last >= r.head => getLineSubstring(g, r)
      case _ => null
    }
  }

  private def getLineSubstring(geom: Geometry, fractions: Seq[Double]): Any = {
    val length = geom.getLength()
    val indexedLine = new LengthIndexedLine(geom)
    val subLine = indexedLine.extractLine(length * fractions.head, length * fractions.last)
    subLine.toGenericArrayData
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Returns a point interpolated along a line. First argument must be a LINESTRING.
 * Second argument is a Double between 0 and 1 representing fraction of
 * total linestring length the point has to be located.
 *
 * @param inputExpressions
 */
case class ST_LineInterpolatePoint(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  assert(inputExpressions.length == 2)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val geometry = inputExpressions(0).toGeometry(input)
    val fraction: Double = inputExpressions(1).eval(input) match {
      case a: Decimal => a.toDouble
      case a: Double => a
      case a: Int => a
    }

    (geometry, fraction) match {
      case (g:LineString, f:Double) if f >= 0 && f <= 1 => getLineInterpolatePoint(g, f)
      case _ => null
    }
  }

  private def getLineInterpolatePoint(geom: Geometry, fraction: Double): Any = {
    val length = geom.getLength()
    val indexedLine = new LengthIndexedLine(geom)
    val interPoint = indexedLine.extractPoint(length * fraction)
    new GeometryFactory().createPoint(interPoint).toGenericArrayData
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_EndPoint(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {

  override protected def nullSafeEval(geometry: Geometry): Any = {
    geometry match {
      case string: LineString => string.getEndPoint.toGenericArrayData
      case _ => null
    }

  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_ExteriorRing(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.exteriorRing) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_GeometryN(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.geometryN) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_InteriorRingN(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.interiorRingN) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_Dump(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override protected def nullSafeEval(geometry: Geometry): Any = {
    val geometryCollection = geometry match {
      case collection: GeometryCollection => {
        val numberOfGeometries = collection.getNumGeometries
        (0 until numberOfGeometries).map(
          index => collection.getGeometryN(index).toGenericArrayData
        ).toArray
      }
      case geom: Geometry => Array(geom.toGenericArrayData)
    }
    ArrayData.toArrayData(geometryCollection)
  }

  override def dataType: DataType = ArrayType(GeometryUDT)

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_DumpPoints(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override protected def nullSafeEval(geometry: Geometry): Any = {
    ArrayData.toArrayData(geometry.getPoints.map(geom => geom.toGenericArrayData))
  }

  override def dataType: DataType = ArrayType(GeometryUDT)

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


case class ST_IsClosed(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.isClosed) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_NumInteriorRings(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.numInteriorRings) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AddPoint(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  inputExpressions.betweenLength(2, 3)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val geometry = inputExpressions.head.toGeometry(input)
    val point = inputExpressions(1).toGeometry(input)
    val geom = if (inputExpressions.length == 2) Functions.addPoint(geometry, point) else {
      val index = inputExpressions(2).toInt(input)
      Functions.addPoint(geometry, point, index)
    }
    geom match {
      case linestring: LineString => linestring.toGenericArrayData
      case _ => null
    }
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_RemovePoint(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  inputExpressions.betweenLength(1, 2)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    val linesString = inputExpressions(0).toGeometry(input)
    val geom = if (inputExpressions.length < 2) Functions.removePoint(linesString) else {
      val index = inputExpressions(1).toInt(input)
      Functions.removePoint(linesString, index)
    }
    geom match {
      case linestring: LineString => linestring.toGenericArrayData
      case _ => null
    }
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_IsRing(inputExpressions: Seq[Expression])
  extends UnaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 1)

  override protected def nullSafeEval(geometry: Geometry): Any = {
    geometry match {
      case string: LineString => Functions.isRing(string)
      case _ => null
    }
  }

  override def dataType: DataType = BooleanType

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Returns the number of Geometries. If geometry is a GEOMETRYCOLLECTION (or MULTI*) return the number of geometries,
  * for single geometries will return 1
  *
  * This method implements the SQL/MM specification. SQL-MM 3: 9.1.4
  *
  * @param inputExpressions Geometry
  */
case class ST_NumGeometries(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.numGeometries) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
  * Returns a version of the given geometry with X and Y axis flipped.
  *
  * @param inputExpressions Geometry
  */
case class ST_FlipCoordinates(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.flipCoordinates) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_SubDivide(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  assert(inputExpressions.length == 2)

  override def nullable: Boolean = true

  override def eval(input: InternalRow): Any = {
    inputExpressions(0).toGeometry(input) match {
      case geom: Geometry => ArrayData.toArrayData(
        GeometrySubDivider.subDivide(geom, inputExpressions(1).toInt(input)).map(_.toGenericArrayData)
      )
      case null => null
    }

  }

  override def dataType: DataType = ArrayType(GeometryUDT)

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_SubDivideExplode(children: Seq[Expression])
  extends Generator with CodegenFallback {
  children.validateLength(2)

  override def eval(input: InternalRow): TraversableOnce[InternalRow] = {
    val geometryRaw = children.head
    val maxVerticesRaw = children(1)
    geometryRaw.toGeometry(input) match {
      case geom: Geometry => ArrayData.toArrayData(
        GeometrySubDivider.subDivide(geom, maxVerticesRaw.toInt(input)).map(_.toGenericArrayData)
      )
        GeometrySubDivider.subDivide(geom, maxVerticesRaw.toInt(input)).map(_.toGenericArrayData).map(InternalRow(_))
      case _ => new Array[InternalRow](0)
    }
  }
  override def elementSchema: StructType = {
    new StructType()
      .add("geom", GeometryUDT, true)
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(children = newChildren)
  }
}


case class ST_MakePolygon(inputExpressions: Seq[Expression])
  extends Expression with CodegenFallback {
  inputExpressions.betweenLength(1, 2)

  override def nullable: Boolean = true
  private val geometryFactory = new GeometryFactory()

  override def eval(input: InternalRow): Any = {
    val exteriorRing = inputExpressions.head
    val possibleHolesRaw = inputExpressions.tail.headOption.map(_.eval(input).asInstanceOf[ArrayData])
    val numOfElements = possibleHolesRaw.map(_.numElements()).getOrElse(0)

    val holes = (0 until numOfElements).map(el => possibleHolesRaw match {
      case Some(value) => Some(value.getArray(el))
      case None => None
    }).filter(_.nonEmpty)
      .map(el => el.map(_.toGeometry))
      .flatMap{
        case maybeLine: Option[LineString] =>
          maybeLine.map(line => geometryFactory.createLinearRing(line.getCoordinates))
        case _ => None
      }

    exteriorRing.toGeometry(input) match {
      case geom: LineString =>
        try {
          val poly = new Polygon(geometryFactory.createLinearRing(geom.getCoordinates), holes.toArray, geometryFactory)
          poly.toGenericArrayData
        }
        catch {
          case e: Exception => null
        }

      case _ => null
    }

  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_GeoHash(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.geohash) with CodegenFallback {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Return the difference between geometry A and B
 *
 * @param inputExpressions
 */
case class ST_Difference(inputExpressions: Seq[Expression])
  extends BinaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 2)

  lazy val GeometryFactory = new GeometryFactory()
  lazy val emptyPolygon = GeometryFactory.createPolygon(null, null)

  override protected def nullSafeEval(leftGeometry: Geometry, rightGeometry: Geometry): Any = {
    val isIntersects = leftGeometry.intersects(rightGeometry)
    lazy val isRightContainsLeft = rightGeometry.contains(leftGeometry)

    if (!isIntersects) {
      new GenericArrayData(GeometrySerializer.serialize(leftGeometry))
    }

    if (isIntersects && isRightContainsLeft) {
      new GenericArrayData(GeometrySerializer.serialize(emptyPolygon))
    }

    new GenericArrayData(GeometrySerializer.serialize(leftGeometry.difference(rightGeometry)))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Return the symmetrical difference between geometry A and B
 *
 * @param inputExpressions
 */
case class ST_SymDifference(inputExpressions: Seq[Expression])
  extends BinaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 2)

  override protected def nullSafeEval(leftGeometry: Geometry, rightGeometry: Geometry): Any = {
    new GenericArrayData(GeometrySerializer.serialize(leftGeometry.symDifference(rightGeometry)))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Return the union of geometry A and B
 *
 * @param inputExpressions
 */
case class ST_Union(inputExpressions: Seq[Expression])
  extends BinaryGeometryExpression with CodegenFallback {
  assert(inputExpressions.length == 2)

  override protected def nullSafeEval(leftGeometry: Geometry, rightGeometry: Geometry): Any = {
    new GenericArrayData(GeometrySerializer.serialize(leftGeometry.union(rightGeometry)))
  }

  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_Multi(inputExpressions: Seq[Expression]) extends UnaryGeometryExpression with CodegenFallback{
  override def dataType: DataType = GeometryUDT

  override def children: Seq[Expression] = inputExpressions

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }

  override protected def nullSafeEval(geometry: Geometry): Any ={
    Collect.createMultiGeometry(Seq(geometry)).toGenericArrayData
  }
}

/**
 * Returns a POINT guaranteed to lie on the surface.
 *
 * @param inputExpressions Geometry
 */
case class ST_PointOnSurface(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.pointOnSurface) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Returns the geometry with vertex order reversed
 *
 * @param inputExpressions
 */
case class ST_Reverse(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.reverse) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Returns the nth point in the geometry, provided it is a linestring
 *
 * @param inputExpressions sequence of 2 input arguments, a geometry and a value 'n'
 */
case class ST_PointN(inputExpressions: Seq[Expression])
  extends InferredBinaryExpression(Functions.pointN) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
      copy(inputExpressions = newChildren)
  }
}

 /*
 * Forces the geometries into a "2-dimensional mode" so that all output representations will only have the X and Y coordinates.
 *
 * @param inputExpressions
 */
case class ST_Force_2D(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.force2D) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Returns the geometry in EWKT format
 *
 * @param inputExpressions
 */
case class ST_AsEWKT(inputExpressions: Seq[Expression])
  extends  InferredUnaryExpression(Functions.asEWKT) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AsGML(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.asGML) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

case class ST_AsKML(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.asKML) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if Geometry is empty geometry.
 *
 * @param inputExpressions
 */
case class ST_IsEmpty(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.isEmpty) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if returning Max X coordinate value.
 *
 * @param inputExpressions
 */
case class ST_XMax(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.xMax) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Test if returning Min X coordinate value.
 *
 * @param inputExpressions
 */
case class ST_XMin(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.xMin) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}


/**
 * Returns the areal geometry formed by the constituent linework of the input geometry assuming all inner geometries represent holes
 *
 * @param inputExpressions
 */
case class ST_BuildArea(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.buildArea) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]): Expression = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Returns the input geometry in its normalized form.
 *
 * @param inputExpressions
 */
case class ST_Normalize(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.normalize) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]): Expression = {
    copy(inputExpressions = newChildren)
  }
}

/**
 * Returns the LineString geometry given a MultiPoint geometry
 *
 * @param inputExpressions
 */
case class ST_LineFromMultiPoint(inputExpressions: Seq[Expression])
  extends InferredUnaryExpression(Functions.lineFromMultiPoint) {

  protected def withNewChildrenInternal(newChildren: IndexedSeq[Expression]) = {
    copy(inputExpressions = newChildren)
  }
}
