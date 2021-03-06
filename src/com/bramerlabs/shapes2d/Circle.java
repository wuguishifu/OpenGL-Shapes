package com.bramerlabs.shapes2d;


import com.bramerlabs.shapes2d.Triangle;
import com.bramerlabs.support.Vector3f;

import java.awt.*;
import java.util.ArrayList;

public class Circle {

    // position of focus of circle - default (0, 0, 0)
    private Vector3f position;

    // radius of circle - default to 1
    private float radius = 1;

    // vector normal to this circle - default <0, 0, 1>
    private Vector3f normal = new Vector3f(0, 0, 1);

    // the color of this circle - default black
    private Color color;

    // the number of triangles used to make this circle - default 20
    private int numTriangles = 120;

    // ArrayList of triangles in the mesh of this circle
    private ArrayList<Triangle> faces = new ArrayList<>();

    // ArrayList of vertices
    private ArrayList<Vector3f> vertices = new ArrayList<>();

    // golden ratio
    private static final float phi = 1.6180339f;

    /**
     * constructor for specified position and color with radius 1
     * generates a circle in the xy-plane
     * @param position - the position of the focus of this circle
     * @param color - the color of this circle
     */
    public Circle(Vector3f position, Color color) {
        this.position = position;
        this.color = color;
        generateTriangles();
    }

    /**
     * constructor for all specified values
     * @param position - the position of the focus of this circle
     * @param radius - the radius of the circle
     * @param normal - a vector normal to the circle
     * @param color - the color of this circle
     * @param numTriangles - the amount of times to recursively subdivide faces for smoothing
     */
    public Circle(Vector3f position, float radius, Vector3f normal, Color color, int numTriangles) {
        this.position = position;
        this.radius = radius;
        this.normal = normal;
        this.color = color;
        this.numTriangles = numTriangles;
        generateTriangles();
    }

    /**
     * populates this.faces with a list of triangles
     */
    private void generateTriangles() {
        // generate the vertices
        vertices = generateVertices();

        // convert the color to a bramerlabs Vector3f
        Vector3f c = new Vector3f(color.getRed(), color.getGreen(), color.getBlue());
        c.scale((float)1/255);

        // create numTriangles-1 bramerlabs triangles using consecutive radial vertices
        for (int i = 1; i < numTriangles; i++) {
            this.faces.add(new Triangle(vertices.get(i), vertices.get(0), vertices.get(i+1), c));
        }

        // create the last triangle using the first and last radial vertices
        this.faces.add(new Triangle(vertices.get(numTriangles), vertices.get(0), vertices.get(1), c));
    }

    /**
     * generates a list of vertices based on the amount of triangles specified - default 20
     * @return a list of vertices of triangles in the sphere. First vertex is the origin.
     */
    private ArrayList<Vector3f> generateVertices() {

        ArrayList<Vector3f> vertices = new ArrayList<>();
        vertices.add(position);

        // generate two orthogonal vectors, v1 and v2, on the plane described by the normal vector
        // take some random vector v0 non-parallel to the normal vector
        Vector3f v0 = new Vector3f(1, 0, 1);
        if (Vector3f.cross(normal, v0).equals(new Vector3f(0, 0, 0), 0.00001f)) {
            v0 = new Vector3f(0, 1, 1);
        }

        Vector3f v1 = Vector3f.cross(normal, v0);
        Vector3f v2 = Vector3f.cross(normal, v1);
        v1.normalize(radius);
        v2.normalize(radius);

        // determine the change in t corresponding to the amount of triangles required
        float dt = ((float)Math.PI * 2)/numTriangles;

        // generate the vertices using the parametric equation
        // r(t) = c + r*cos(t)*i + r*sin(t)*j, where i, j are v1, v2 and r is radius
        // r(t) for 0 <= t <= 2pi
        for (int i = 0; i < numTriangles; i++) {
            Vector3f v = (new Vector3f(v1)).scale((float) (radius * Math.cos(i * dt)));
            v.add((new Vector3f(v2)).scale((float) (radius * Math.sin(i * dt))));
            v.normalize(radius);
            v.add(position);
            vertices.add(v);
        }

        return vertices;
    }

    /**
     * getter method
     * @return - the ArrayList of triangles making up the mesh of this circle
     */
    public ArrayList<Triangle> getFaces() {
        return this.faces;
    }

    /**
     * getter method
     * @return - the ArrayList of vertices in this circle
     */
    public ArrayList<Vector3f> getVertices() {
        return this.vertices;
    }

    /**
     * getter method
     * @return - a float array containing every triangle
     */
    public float[] getFacesAsFloats() {
        float[] f = new float[numTriangles*9];
        for (int i = 0; i < numTriangles; i++) {
            f[i * 9] = faces.get(i).getV1().toFloats()[0];
            f[i * 9 + 1] = faces.get(i).getV1().toFloats()[1];
            f[i * 9 + 2] = faces.get(i).getV1().toFloats()[2];
            f[i * 9 + 3] = faces.get(i).getV2().toFloats()[0];
            f[i * 9 + 4] = faces.get(i).getV2().toFloats()[1];
            f[i * 9 + 5] = faces.get(i).getV2().toFloats()[2];
            f[i * 9 + 6] = faces.get(i).getV3().toFloats()[0];
            f[i * 9 + 7] = faces.get(i).getV3().toFloats()[1];
            f[i * 9 + 8] = faces.get(i).getV3().toFloats()[2];
        }
        return f;
    }

}