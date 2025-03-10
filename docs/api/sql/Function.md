## ST_3DDistance

Introduction: Return the 3-dimensional minimum cartesian distance between A and B

Format: `ST_3DDistance (A:geometry, B:geometry)`

Since: `v1.2.0`

Spark SQL example:
```SQL
SELECT ST_3DDistance(polygondf.countyshape, polygondf.countyshape)
FROM polygondf
```

## ST_AddPoint

Introduction: RETURN Linestring with additional point at the given index, if position is not available the point will be added at the end of line.

Format: `ST_AddPoint(geom: geometry, point: geometry, position: integer)`

Format: `ST_AddPoint(geom: geometry, point: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_AddPoint(ST_GeomFromText("LINESTRING(0 0, 1 1, 1 0)"), ST_GeomFromText("Point(21 52)"), 1)

SELECT ST_AddPoint(ST_GeomFromText("Linestring(0 0, 1 1, 1 0)"), ST_GeomFromText("Point(21 52)"))
```

Output:
```
LINESTRING(0 0, 21 52, 1 1, 1 0)
LINESTRING(0 0, 1 1, 1 0, 21 52)
```

## ST_Area

Introduction: Return the area of A

Format: `ST_Area (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Area(polygondf.countyshape)
FROM polygondf
```

## ST_AsBinary

Introduction: Return the Well-Known Binary representation of a geometry

Format: `ST_AsBinary (A:geometry)`

Since: `v1.1.1`

Spark SQL example:
```SQL
SELECT ST_AsBinary(polygondf.countyshape)
FROM polygondf
```

## ST_AsEWKB

Introduction: Return the Extended Well-Known Binary representation of a geometry.
EWKB is an extended version of WKB which includes the SRID of the geometry.
The format originated in PostGIS but is supported by many GIS tools.
If the geometry is lacking SRID a WKB format is produced.
[Se ST_SetSRID](#ST_SetSRID)

Format: `ST_AsEWKB (A:geometry)`

Since: `v1.1.1`

Spark SQL example:
```SQL
SELECT ST_AsEWKB(polygondf.countyshape)
FROM polygondf
```

## ST_AsEWKT

Introduction: Return the Extended Well-Known Text representation of a geometry.
EWKT is an extended version of WKT which includes the SRID of the geometry.
The format originated in PostGIS but is supported by many GIS tools.
If the geometry is lacking SRID a WKT format is produced.
[See ST_SetSRID](#ST_SetSRID)

Format: `ST_AsEWKT (A:geometry)`

Since: `v1.2.1`

Spark SQL example:
```SQL
SELECT ST_AsEWKT(polygondf.countyshape)
FROM polygondf
```

## ST_AsGeoJSON

Introduction: Return the [GeoJSON](https://geojson.org/) string representation of a geometry

Format: `ST_AsGeoJSON (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_AsGeoJSON(polygondf.countyshape)
FROM polygondf
```

## ST_AsGML

Introduction: Return the [GML](https://www.ogc.org/standards/gml) string representation of a geometry

Format: `ST_AsGML (A:geometry)`

Since: `v1.3.0`

Spark SQL example:
```SQL
SELECT ST_AsGML(polygondf.countyshape)
FROM polygondf
```

## ST_AsKML

Introduction: Return the [KML](https://www.ogc.org/standards/kml) string representation of a geometry

Format: `ST_AsKML (A:geometry)`

Since: `v1.3.0`

Spark SQL example:
```SQL
SELECT ST_AsKML(polygondf.countyshape)
FROM polygondf
```

## ST_AsText

Introduction: Return the Well-Known Text string representation of a geometry

Format: `ST_AsText (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_AsText(polygondf.countyshape)
FROM polygondf
```

## ST_Azimuth

Introduction: Returns Azimuth for two given points in radians null otherwise.

Format: `ST_Azimuth(pointA: Point, pointB: Point)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Azimuth(ST_POINT(0.0, 25.0), ST_POINT(0.0, 0.0))
```

Output: `3.141592653589793`

## ST_Boundary

Introduction: Returns the closure of the combinatorial boundary of this Geometry.

Format: `ST_Boundary(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Boundary(ST_GeomFromText('POLYGON((1 1,0 0, -1 1, 1 1))'))
```

Output: `LINESTRING (1 1, 0 0, -1 1, 1 1)`

## ST_Buffer

Introduction: Returns a geometry/geography that represents all points whose distance from this Geometry/geography is less than or equal to distance.

Format: `ST_Buffer (A:geometry, buffer: Double)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Buffer(polygondf.countyshape, 1)
FROM polygondf
```

## ST_BuildArea

Introduction: Returns the areal geometry formed by the constituent linework of the input geometry.

Format: `ST_BuildArea (A:geometry)`

Since: `v1.2.1`

Example:

```SQL
SELECT ST_BuildArea(
    ST_GeomFromText('MULTILINESTRING((0 0, 20 0, 20 20, 0 20, 0 0),(2 2, 18 2, 18 18, 2 18, 2 2))')
) AS geom
```

Result:

```

+----------------------------------------------------------------------------+
|geom                                                                        |
+----------------------------------------------------------------------------+
|POLYGON((0 0,0 20,20 20,20 0,0 0),(2 2,18 2,18 18,2 18,2 2))                |
+----------------------------------------------------------------------------+
```

## ST_Centroid

Introduction: Return the centroid point of A

Format: `ST_Centroid (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Centroid(polygondf.countyshape)
FROM polygondf
```

## ST_Collect

Introduction: Returns MultiGeometry object based on geometry column/s or array with geometries

Format

`ST_Collect(*geom: geometry)`

`ST_Collect(geom: array<geometry>)`

Since: `v1.2.0`

Example:

```SQL
SELECT ST_Collect(
    ST_GeomFromText('POINT(21.427834 52.042576573)'),
    ST_GeomFromText('POINT(45.342524 56.342354355)')
) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|MULTIPOINT ((21.427834 52.042576573), (45.342524 56.342354355))|
+---------------------------------------------------------------+
```

Example:

```SQL
SELECT ST_Collect(
    Array(
        ST_GeomFromText('POINT(21.427834 52.042576573)'),
        ST_GeomFromText('POINT(45.342524 56.342354355)')
    )
) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|MULTIPOINT ((21.427834 52.042576573), (45.342524 56.342354355))|
+---------------------------------------------------------------+
```

## ST_CollectionExtract

Introduction: Returns a homogeneous multi-geometry from a given geometry collection.

The type numbers are:
1. POINT
2. LINESTRING
3. POLYGON

If the type parameter is omitted a multi-geometry of the highest dimension is returned.

Format: `ST_CollectionExtract (A:geometry)`

Format: `ST_CollectionExtract (A:geometry, type:Int)`

Since: `v1.2.1`

Example:

```SQL
WITH test_data as (
    ST_GeomFromText(
        'GEOMETRYCOLLECTION(POINT(40 10), POLYGON((0 0, 0 5, 5 5, 5 0, 0 0)))'
    ) as geom
)
SELECT ST_CollectionExtract(geom) as c1, ST_CollectionExtract(geom, 1) as c2 
FROM test_data

```

Result:

```
+----------------------------------------------------------------------------+
|c1                                        |c2                               |
+----------------------------------------------------------------------------+
|MULTIPOLYGON(((0 0, 0 5, 5 5, 5 0, 0 0))) |MULTIPOINT(40 10)                |              |
+----------------------------------------------------------------------------+
```

## ST_ConvexHull

Introduction: Return the Convex Hull of polgyon A

Format: `ST_ConvexHull (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_ConvexHull(polygondf.countyshape)
FROM polygondf
```

## ST_Difference

Introduction: Return the difference between geometry A and B (return part of geometry A that does not intersect geometry B)

Format: `ST_Difference (A:geometry, B:geometry)`

Since: `v1.2.0`

Example:

```SQL
SELECT ST_Difference(ST_GeomFromWKT('POLYGON ((-3 -3, 3 -3, 3 3, -3 3, -3 -3))'), ST_GeomFromWKT('POLYGON ((0 -4, 4 -4, 4 4, 0 4, 0 -4))'))
```

Result:

```
POLYGON ((0 -3, -3 -3, -3 3, 0 3, 0 -3))
```

## ST_Distance

Introduction: Return the Euclidean distance between A and B

Format: `ST_Distance (A:geometry, B:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Distance(polygondf.countyshape, polygondf.countyshape)
FROM polygondf
```

## ST_Dump

Introduction: It expands the geometries. If the geometry is simple (Point, Polygon Linestring etc.) it returns the geometry
itself, if the geometry is collection or multi it returns record for each of collection components.

Format: `ST_Dump(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Dump(ST_GeomFromText('MULTIPOINT ((10 40), (40 30), (20 20), (30 10))'))
```

Output: `[POINT (10 40), POINT (40 30), POINT (20 20), POINT (30 10)]`

## ST_DumpPoints

Introduction: Returns list of Points which geometry consists of.

Format: `ST_DumpPoints(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_DumpPoints(ST_GeomFromText('LINESTRING (0 0, 1 1, 1 0)')) 
```

Output: `[POINT (0 0), POINT (0 1), POINT (1 1), POINT (1 0), POINT (0 0)]`

## ST_EndPoint

Introduction: Returns last point of given linestring.

Format: `ST_EndPoint(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_EndPoint(ST_GeomFromText('LINESTRING(100 150,50 60, 70 80, 160 170)'))
```

Output: `POINT(160 170)`

## ST_Envelope

Introduction: Return the envelop boundary of A

Format: `ST_Envelope (A:geometry)`

Since: `v1.0.0`

Spark SQL example:

```SQL
SELECT ST_Envelope(polygondf.countyshape)
FROM polygondf
```

## ST_ExteriorRing

Introduction: Returns a line string representing the exterior ring of the POLYGON geometry. Return NULL if the geometry is not a polygon.

Format: `ST_ExteriorRing(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_ExteriorRing(ST_GeomFromText('POLYGON((0 0 1, 1 1 1, 1 2 1, 1 1 1, 0 0 1))'))
```

Output: `LINESTRING (0 0, 1 1, 1 2, 1 1, 0 0)`

## ST_FlipCoordinates

Introduction: Returns a version of the given geometry with X and Y axis flipped.

Format: `ST_FlipCoordinates(A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_FlipCoordinates(df.geometry)
FROM df
```

Input: `POINT (1 2)`

Output: `POINT (2 1)`

## ST_Force_2D

Introduction: Forces the geometries into a "2-dimensional mode" so that all output representations will only have the X and Y coordinates

Format: `ST_Force_2D (A:geometry)`

Since: `v1.2.1`

Example:

```SQL
SELECT ST_AsText(
    ST_Force_2D(ST_GeomFromText('POLYGON((0 0 2,0 5 2,5 0 2,0 0 2),(1 1 2,3 1 2,1 3 2,1 1 2))'))
) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|POLYGON((0 0,0 5,5 0,0 0),(1 1,3 1,1 3,1 1))                   |
+---------------------------------------------------------------+
```

## ST_GeoHash

Introduction: Returns GeoHash of the geometry with given precision

Format: `ST_GeoHash(geom: geometry, precision: int)`

Since: `v1.1.1`

Example:

Query:

```SQL
SELECT ST_GeoHash(ST_GeomFromText('POINT(21.427834 52.042576573)'), 5) AS geohash
```

Result:

```
+-----------------------------+
|geohash                      |
+-----------------------------+
|u3r0p                        |
+-----------------------------+
```

## ST_GeometryN

Introduction: Return the 0-based Nth geometry if the geometry is a GEOMETRYCOLLECTION, (MULTI)POINT, (MULTI)LINESTRING, MULTICURVE or (MULTI)POLYGON. Otherwise, return null

Format: `ST_GeometryN(geom: geometry, n: Int)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_GeometryN(ST_GeomFromText('MULTIPOINT((1 2), (3 4), (5 6), (8 9))'), 1)
```

Output: `POINT (3 4)`

## ST_GeometryType

Introduction: Returns the type of the geometry as a string. EG: 'ST_Linestring', 'ST_Polygon' etc.

Format: `ST_GeometryType (A:geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_GeometryType(polygondf.countyshape)
FROM polygondf
```

## ST_InteriorRingN

Introduction: Returns the Nth interior linestring ring of the polygon geometry. Returns NULL if the geometry is not a polygon or the given N is out of range

Format: `ST_InteriorRingN(geom: geometry, n: Int)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_InteriorRingN(ST_GeomFromText('POLYGON((0 0, 0 5, 5 5, 5 0, 0 0), (1 1, 2 1, 2 2, 1 2, 1 1), (1 3, 2 3, 2 4, 1 4, 1 3), (3 3, 4 3, 4 4, 3 4, 3 3))'), 0)
```

Output: `LINESTRING (1 1, 2 1, 2 2, 1 2, 1 1)`

## ST_Intersection

Introduction: Return the intersection geometry of A and B

Format: `ST_Intersection (A:geometry, B:geometry)`

Since: `v1.0.0`

Spark SQL example:

```SQL
SELECT ST_Intersection(polygondf.countyshape, polygondf.countyshape)
FROM polygondf
```

## ST_IsClosed

Introduction: RETURNS true if the LINESTRING start and end point are the same.

Format: `ST_IsClosed(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_IsClosed(ST_GeomFromText('LINESTRING(0 0, 1 1, 1 0)'))
```

Output: `false`

## ST_IsEmpty

Introduction: Test if a geometry is empty geometry

Format: `ST_IsEmpty (A:geometry)`

Since: `v1.2.1`

Spark SQL example:

```SQL
SELECT ST_IsEmpty(polygondf.countyshape)
FROM polygondf
```

## ST_IsRing

Introduction: RETURN true if LINESTRING is ST_IsClosed and ST_IsSimple.

Format: `ST_IsRing(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_IsRing(ST_GeomFromText("LINESTRING(0 0, 0 1, 1 1, 1 0, 0 0)"))
```

Output: `true`

## ST_IsSimple

Introduction: Test if geometry's only self-intersections are at boundary points.

Format: `ST_IsSimple (A:geometry)`

Since: `v1.0.0`

Spark SQL example:

```SQL
SELECT ST_IsSimple(polygondf.countyshape)
FROM polygondf
```

## ST_IsValid

Introduction: Test if a geometry is well formed

Format: `ST_IsValid (A:geometry)`

Since: `v1.0.0`

Spark SQL example:

```SQL
SELECT ST_IsValid(polygondf.countyshape)
FROM polygondf
```

## ST_Length

Introduction: Return the perimeter of A

Format: ST_Length (A:geometry)

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Length(polygondf.countyshape)
FROM polygondf
```

## ST_LineFromMultiPoint

Introduction: Creates a LineString from a MultiPoint geometry.

Format: `ST_LineFromMultiPoint (A:geometry)`

Since: `v1.3.0`

Example:

```SQL
SELECT ST_AsText(
    ST_LineFromMultiPoint(ST_GeomFromText('MULTIPOINT((10 40), (40 30), (20 20), (30 10))'))
) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|LINESTRING (10 40, 40 30, 20 20, 30 10)                        |
+---------------------------------------------------------------+
```

## ST_LineInterpolatePoint

Introduction: Returns a point interpolated along a line. First argument must be a LINESTRING. Second argument is a Double between 0 and 1 representing fraction of total linestring length the point has to be located.

Format: `ST_LineInterpolatePoint (geom: geometry, fraction: Double)`

Since: `v1.0.1`

Spark SQL example:
```SQL
SELECT ST_LineInterpolatePoint(ST_GeomFromWKT('LINESTRING(25 50, 100 125, 150 190)'), 0.2) as Interpolated
```

Output:
```
+-----------------------------------------+
|Interpolated                             |
+-----------------------------------------+
|POINT (51.5974135047432 76.5974135047432)|
+-----------------------------------------+
```

## ST_LineMerge

Introduction: Returns a LineString formed by sewing together the constituent line work of a MULTILINESTRING.

!!!note
Only works for MULTILINESTRING. Using other geometry will return a GEOMETRYCOLLECTION EMPTY. If the MultiLineString can't be merged, the original MULTILINESTRING is returned.

Format: `ST_LineMerge (A:geometry)`

Since: `v1.0.0`

```SQL
SELECT ST_LineMerge(geometry)
FROM df
```

## ST_LineSubstring

Introduction: Return a linestring being a substring of the input one starting and ending at the given fractions of total 2d length. Second and third arguments are Double values between 0 and 1. This only works with LINESTRINGs.

Format: `ST_LineSubstring (geom: geometry, startfraction: Double, endfraction: Double)`

Since: `v1.0.1`

Spark SQL example:
```SQL
SELECT ST_LineSubstring(ST_GeomFromWKT('LINESTRING(25 50, 100 125, 150 190)'), 0.333, 0.666) as Substring
```

Output:
```
+------------------------------------------------------------------------------------------------+
|Substring                                                                                       |
+------------------------------------------------------------------------------------------------+
|LINESTRING (69.28469348539744 94.28469348539744, 100 125, 111.70035626068274 140.21046313888758)|
+------------------------------------------------------------------------------------------------+
```

## ST_MakePolygon

Introduction: Function to convert closed linestring to polygon including holes

Format: `ST_MakePolygon(geom: geometry, holes: array<geometry>)`

Since: `v1.1.0`

Example:

Query:
```SQL
SELECT
    ST_MakePolygon(
        ST_GeomFromText('LINESTRING(7 -1, 7 6, 9 6, 9 1, 7 -1)'),
        ARRAY(ST_GeomFromText('LINESTRING(6 2, 8 2, 8 1, 6 1, 6 2)'))
    ) AS polygon
```

Result:

```
+----------------------------------------------------------------+
|polygon                                                         |
+----------------------------------------------------------------+
|POLYGON ((7 -1, 7 6, 9 6, 9 1, 7 -1), (6 2, 8 2, 8 1, 6 1, 6 2))|
+----------------------------------------------------------------+

```

## ST_MakeValid

Introduction: Given an invalid geometry, create a valid representation of the geometry.

Collapsed geometries are either converted to empty (keepCollaped=true) or a valid geometry of lower dimension (keepCollapsed=false).
Default is keepCollapsed=false.

Format: `ST_MakeValid (A:geometry)`

Format: `ST_MakeValid (A:geometry, keepCollapsed:Boolean)`

Since: `v1.0.0`

Spark SQL example:

```SQL
WITH linestring AS (
    SELECT ST_GeomFromWKT('LINESTRING(1 1, 1 1)') AS geom
) SELECT ST_MakeValid(geom), ST_MakeValid(geom, true) FROM linestring
```

Result:
```
+------------------+------------------------+
|st_makevalid(geom)|st_makevalid(geom, true)|
+------------------+------------------------+
|  LINESTRING EMPTY|             POINT (1 1)|
+------------------+------------------------+
```

!!!note
In Sedona up to and including version 1.2 the behaviour of ST_MakeValid was different.
Be sure to check you code when upgrading.
The previous implementation only worked for (multi)polygons and had a different interpretation of the second, boolean, argument.
It would also sometimes return multiple geometries for a single geomtry input.

## ST_MinimumBoundingCircle

Introduction: Returns the smallest circle polygon that contains a geometry.

Format: `ST_MinimumBoundingCircle(geom: geometry, [Optional] quadrantSegments:int)`

Since: `v1.0.1`

Spark SQL example:
```SQL
SELECT ST_MinimumBoundingCircle(ST_GeomFromText('POLYGON((1 1,0 0, -1 1, 1 1))'))
```

## ST_MinimumBoundingRadius

Introduction: Returns a struct containing the center point and radius of the smallest circle that contains a geometry.

Format: `ST_MinimumBoundingRadius(geom: geometry)`

Since: `v1.0.1`

Spark SQL example:
```SQL
SELECT ST_MinimumBoundingRadius(ST_GeomFromText('POLYGON((1 1,0 0, -1 1, 1 1))'))
```

## ST_Multi

Introduction: Returns a MultiGeometry object based on the geometry input.
ST_Multi is basically an alias for ST_Collect with one geometry.

Format

`ST_Multi(geom: geometry)`

Since: `v1.2.0`

Example:

```SQL
SELECT ST_Multi(
    ST_GeomFromText('POINT(1 1)')
) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|MULTIPOINT (1 1)                                               |
+---------------------------------------------------------------+
```

## ST_Normalize

Introduction: Returns the input geometry in its normalized form.

Format

`ST_Normalize(geom: geometry)`

Since: `v1.3.0`

Example:

```SQL
SELECT ST_AsEWKT(ST_Normalize(ST_GeomFromWKT('POLYGON((0 1, 1 1, 1 0, 0 0, 0 1))'))) AS geom
```

Result:

```
+-----------------------------------+
|geom                               |
+-----------------------------------+
|POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))|
+-----------------------------------+
```

## ST_NPoints

Introduction: Return points of the geometry

Since: `v1.0.0`

Format: `ST_NPoints (A:geometry)`

```SQL
SELECT ST_NPoints(polygondf.countyshape)
FROM polygondf
```

## ST_NumGeometries

Introduction: Returns the number of Geometries. If geometry is a GEOMETRYCOLLECTION (or MULTI*) return the number of geometries, for single geometries will return 1.

Format: `ST_NumGeometries (A:geometry)`

Since: `v1.0.0`

```SQL
SELECT ST_NumGeometries(df.geometry)
FROM df
```

## ST_NumInteriorRings

Introduction: RETURNS number of interior rings of polygon geometries.

Format: `ST_NumInteriorRings(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_NumInteriorRings(ST_GeomFromText('POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0), (1 1, 2 1, 2 2, 1 2, 1 1))'))
```

Output: `1`

## ST_PointN

Introduction: Return the Nth point in a single linestring or circular linestring in the geometry. Negative values are counted backwards from the end of the LineString, so that -1 is the last point. Returns NULL if there is no linestring in the geometry.

Format: `ST_PointN(geom: geometry, n: integer)`

Since: `v1.2.1`

Spark SQL example:
```SQL
SELECT ST_PointN(ST_GeomFromText("LINESTRING(0 0, 1 2, 2 4, 3 6)"), 2) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|POINT (1 2)                                                    |
+---------------------------------------------------------------+
```

## ST_PointOnSurface

Introduction: Returns a POINT guaranteed to lie on the surface.

Format: `ST_PointOnSurface(A:geometry)`

Since: `v1.2.1`

Examples:

```
SELECT ST_AsText(ST_PointOnSurface(ST_GeomFromText('POINT(0 5)')));
 st_astext
------------
 POINT(0 5)

SELECT ST_AsText(ST_PointOnSurface(ST_GeomFromText('LINESTRING(0 5, 0 10)')));
 st_astext
------------
 POINT(0 5)

SELECT ST_AsText(ST_PointOnSurface(ST_GeomFromText('POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))')));
   st_astext
----------------
 POINT(2.5 2.5)

SELECT ST_AsText(ST_PointOnSurface(ST_GeomFromText('LINESTRING(0 5 1, 0 0 1, 0 10 2)')));
   st_astext
----------------
 POINT Z(0 0 1)

```

## ST_PrecisionReduce

Introduction: Reduce the decimals places in the coordinates of the geometry to the given number of decimal places. The last decimal place will be rounded.

Format: `ST_PrecisionReduce (A:geometry, B:int)`

Since: `v1.0.0`

Spark SQL example:

```SQL
SELECT ST_PrecisionReduce(polygondf.countyshape, 9)
FROM polygondf
```
The new coordinates will only have 9 decimal places.

## ST_RemovePoint

Introduction: RETURN Line with removed point at given index, position can be omitted and then last one will be removed.

Format: `ST_RemovePoint(geom: geometry, position: integer)`

Format: `ST_RemovePoint(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_RemovePoint(ST_GeomFromText("LINESTRING(0 0, 1 1, 1 0)"), 1)
```

Output: `LINESTRING(0 0, 1 0)`

## ST_Reverse

Introduction: Return the geometry with vertex order reversed

Format: `ST_Reverse (A:geometry)`

Since: `v1.2.1`

Example:

```SQL
SELECT ST_AsText(
    ST_Reverse(ST_GeomFromText('LINESTRING(0 0, 1 2, 2 4, 3 6)'))
) AS geom
```

Result:

```
+---------------------------------------------------------------+
|geom                                                           |
+---------------------------------------------------------------+
|LINESTRING (3 6, 2 4, 1 2, 0 0)                                |
+---------------------------------------------------------------+
```

## ST_SetSRID

Introduction: Sets the spatial refence system identifier (SRID) of the geometry.

Format: `ST_SetSRID (A:geometry, srid: Integer)`

Since: `v1.1.1`

Spark SQL example:
```SQL
SELECT ST_SetSRID(polygondf.countyshape, 3021)
FROM polygondf
```

## ST_SimplifyPreserveTopology

Introduction: Simplifies a geometry and ensures that the result is a valid geometry having the same dimension and number of components as the input,
and with the components having the same topological relationship.

Since: `v1.0.0`

Format: `ST_SimplifyPreserveTopology (A:geometry, distanceTolerance: Double)`

```SQL
SELECT ST_SimplifyPreserveTopology(polygondf.countyshape, 10.0)
FROM polygondf
```

## ST_SRID

Introduction: Return the spatial refence system identifier (SRID) of the geometry.

Format: `ST_SRID (A:geometry)`

Since: `v1.1.1`

Spark SQL example:
```SQL
SELECT ST_SRID(polygondf.countyshape)
FROM polygondf
```

## ST_StartPoint

Introduction: Returns first point of given linestring.

Format: `ST_StartPoint(geom: geometry)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_StartPoint(ST_GeomFromText('LINESTRING(100 150,50 60, 70 80, 160 170)'))
```

Output: `POINT(100 150)`

## ST_SubDivide

Introduction: Returns list of geometries divided based of given maximum number of vertices.

Format: `ST_SubDivide(geom: geometry, maxVertices: int)`

Since: `v1.1.0`

Spark SQL example:
```SQL
SELECT ST_SubDivide(ST_GeomFromText("POLYGON((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))"), 5)

```

Output:
```
[
    POLYGON((37.857142857142854 20, 35 10, 10 20, 37.857142857142854 20)),
    POLYGON((15 20, 10 20, 15 40, 15 20)),
    POLYGON((20 20, 15 20, 15 30, 20 30, 20 20)),
    POLYGON((26.428571428571427 20, 20 20, 20 30, 26.4285714 23.5714285, 26.4285714 20)),
    POLYGON((15 30, 15 40, 20 40, 20 30, 15 30)),
    POLYGON((20 40, 26.4285714 40, 26.4285714 32.1428571, 20 30, 20 40)),
    POLYGON((37.8571428 20, 30 20, 34.0476190 32.1428571, 37.8571428 32.1428571, 37.8571428 20)),
    POLYGON((34.0476190 34.6825396, 26.4285714 32.1428571, 26.4285714 40, 34.0476190 40, 34.0476190 34.6825396)),
    POLYGON((34.0476190 32.1428571, 35 35, 37.8571428 35, 37.8571428 32.1428571, 34.0476190 32.1428571)),
    POLYGON((35 35, 34.0476190 34.6825396, 34.0476190 35, 35 35)),
    POLYGON((34.0476190 35, 34.0476190 40, 37.8571428 40, 37.8571428 35, 34.0476190 35)),
    POLYGON((30 20, 26.4285714 20, 26.4285714 23.5714285, 30 20)),
    POLYGON((15 40, 37.8571428 43.8095238, 37.8571428 40, 15 40)),
    POLYGON((45 45, 37.8571428 20, 37.8571428 43.8095238, 45 45))
]
```

Spark SQL example:

```SQL
SELECT ST_SubDivide(ST_GeomFromText("LINESTRING(0 0, 85 85, 100 100, 120 120, 21 21, 10 10, 5 5)"), 5)
```

Output:
```
[
    LINESTRING(0 0, 5 5)
    LINESTRING(5 5, 10 10)
    LINESTRING(10 10, 21 21)
    LINESTRING(21 21, 60 60)
    LINESTRING(60 60, 85 85)
    LINESTRING(85 85, 100 100)
    LINESTRING(100 100, 120 120)
]
```

## ST_SubDivideExplode

Introduction: It works the same as ST_SubDivide but returns new rows with geometries instead of list.

Format: `ST_SubDivideExplode(geom: geometry, maxVertices: int)`

Since: `v1.1.0`

Example:

Query:
```SQL
SELECT ST_SubDivideExplode(ST_GeomFromText("LINESTRING(0 0, 85 85, 100 100, 120 120, 21 21, 10 10, 5 5)"), 5)
```

Result:

```
+-----------------------------+
|geom                         |
+-----------------------------+
|LINESTRING(0 0, 5 5)         |
|LINESTRING(5 5, 10 10)       |
|LINESTRING(10 10, 21 21)     |
|LINESTRING(21 21, 60 60)     |
|LINESTRING(60 60, 85 85)     |
|LINESTRING(85 85, 100 100)   |
|LINESTRING(100 100, 120 120) |
+-----------------------------+
```

Using Lateral View

Table:
```
+-------------------------------------------------------------+
|geometry                                                     |
+-------------------------------------------------------------+
|LINESTRING(0 0, 85 85, 100 100, 120 120, 21 21, 10 10, 5 5)  |  
+-------------------------------------------------------------+
```

Query
```SQL 
select geom from geometries LATERAL VIEW ST_SubdivideExplode(geometry, 5) AS geom
```

Result:
```
+-----------------------------+
|geom                         |
+-----------------------------+
|LINESTRING(0 0, 5 5)         |
|LINESTRING(5 5, 10 10)       |
|LINESTRING(10 10, 21 21)     |
|LINESTRING(21 21, 60 60)     |
|LINESTRING(60 60, 85 85)     |
|LINESTRING(85 85, 100 100)   |
|LINESTRING(100 100, 120 120) |
+-----------------------------+
```

## ST_SymDifference

Introduction: Return the symmetrical difference between geometry A and B (return parts of geometries which are in either of the sets, but not in their intersection)


Format: `ST_SymDifference (A:geometry, B:geometry)`

Since: `v1.2.0`

Example:

```SQL
SELECT ST_SymDifference(ST_GeomFromWKT('POLYGON ((-3 -3, 3 -3, 3 3, -3 3, -3 -3))'), ST_GeomFromWKT('POLYGON ((-2 -3, 4 -3, 4 3, -2 3, -2 -3))'))
```

Result:

```
MULTIPOLYGON (((-2 -3, -3 -3, -3 3, -2 3, -2 -3)), ((3 -3, 3 3, 4 3, 4 -3, 3 -3)))
```

## ST_Transform

Introduction:

Transform the Spatial Reference System / Coordinate Reference System of A, from SourceCRS to TargetCRS

!!!note
	By default, this function uses lat/lon order. You can use ==ST_FlipCoordinates== to swap X and Y.

!!!note
	If ==ST_Transform== throws an Exception called "Bursa wolf parameters required", you need to disable the error notification in ST_Transform. You can append a boolean value at the end.

Format: `ST_Transform (A:geometry, SourceCRS:string, TargetCRS:string ,[Optional] DisableError)`

Since: `v1.0.0`

Spark SQL example (simple):
```SQL
SELECT ST_Transform(polygondf.countyshape, 'epsg:4326','epsg:3857') 
FROM polygondf
```

Spark SQL example (with optional parameters):
```SQL
SELECT ST_Transform(polygondf.countyshape, 'epsg:4326','epsg:3857', false)
FROM polygondf
```

!!!note
	The detailed EPSG information can be searched on [EPSG.io](https://epsg.io/).

## ST_Union

Introduction: Return the union of geometry A and B


Format: `ST_Union (A:geometry, B:geometry)`

Since: `v1.2.0`

Example:

```SQL
SELECT ST_Union(ST_GeomFromWKT('POLYGON ((-3 -3, 3 -3, 3 3, -3 3, -3 -3))'), ST_GeomFromWKT('POLYGON ((1 -2, 5 0, 1 2, 1 -2))'))
```

Result:

```
POLYGON ((3 -1, 3 -3, -3 -3, -3 3, 3 3, 3 1, 5 0, 3 -1))
```

## ST_X

Introduction: Returns X Coordinate of given Point null otherwise.

Format: `ST_X(pointA: Point)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_X(ST_POINT(0.0 25.0))
```

Output: `0.0`

## ST_XMax

Introduction: Returns the maximum X coordinate of a geometry

Format: `ST_XMax (A:geometry)`

Since: `v1.2.1`

Example:

```SQL
SELECT ST_XMax(df.geometry) AS xmax
FROM df
```

Input: `POLYGON ((-1 -11, 0 10, 1 11, 2 12, -1 -11))`

Output: `2`

## ST_XMin

Introduction: Returns the minimum X coordinate of a geometry

Format: `ST_XMin (A:geometry)`

Since: `v1.2.1`

Example:

```SQL
SELECT ST_XMin(df.geometry) AS xmin
FROM df
```

Input: `POLYGON ((-1 -11, 0 10, 1 11, 2 12, -1 -11))`

Output: `-1`

## ST_Y

Introduction: Returns Y Coordinate of given Point, null otherwise.

Format: `ST_Y(pointA: Point)`

Since: `v1.0.0`

Spark SQL example:
```SQL
SELECT ST_Y(ST_POINT(0.0 25.0))
```

Output: `25.0`

## ST_YMax

Introduction: Return the minimum Y coordinate of A

Format: `ST_YMax (A:geometry)`

Since: `v1.2.1`

Spark SQL example:
```SQL
SELECT ST_YMax(ST_GeomFromText('POLYGON((0 0 1, 1 1 1, 1 2 1, 1 1 1, 0 0 1))'))
```

Output: 2

## ST_YMin

Introduction: Return the minimum Y coordinate of A

Format: `ST_Y_Min (A:geometry)`

Since: `v1.2.1`

Spark SQL example:
```SQL
SELECT ST_YMin(ST_GeomFromText('POLYGON((0 0 1, 1 1 1, 1 2 1, 1 1 1, 0 0 1))'))
```

Output : 0

## ST_Z

Introduction: Returns Z Coordinate of given Point, null otherwise.

Format: `ST_Z(pointA: Point)`

Since: `v1.2.0`

Spark SQL example:
```SQL
SELECT ST_Z(ST_POINT(0.0 25.0 11.0))
```

Output: `11.0`
