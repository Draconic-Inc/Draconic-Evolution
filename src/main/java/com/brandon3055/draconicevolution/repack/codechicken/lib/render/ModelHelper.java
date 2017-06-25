package com.brandon3055.draconicevolution.repack.codechicken.lib.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Various helpers for CCModels
 * <p>
 * Created by covers1624 on 21/06/2017.
 */
//TODO Remove this when CCL3 drops.
public class ModelHelper {

    /**
     * Quadulates a CCModel.
     * Only Position and UV data is copied.
     *
     * @param inModel The in model.
     * @return The new model.
     */
    public static CCModel quadulate(CCModel inModel) {
        if (inModel.vertexMode == 7) {
            throw new IllegalArgumentException("Cannot quadulate a quad model. Check if the model is triangles before calling this");
        }
        LinkedList<Vertex5> inVerts = new LinkedList<>();
        Collections.addAll(inVerts, inModel.getVertices());
        List<Vertex5> verts = new LinkedList<>();
        Iterator<Vertex5> iter = inVerts.iterator();
        while (iter.hasNext()) {
            verts.add(iter.next());
            verts.add(iter.next());
            Vertex5 copy = iter.next();
            verts.add(copy);
            verts.add(copy.copy());
        }
        CCModel outModel = CCModel.quadModel(verts.size());
        outModel.verts = verts.toArray(new Vertex5[0]);
        return outModel;
    }


    /**
     * Attempts to combine square faces.
     * A NEW model is returned with combined faces.
     * You WILL need to copy / compute normals yourself.
     *
     * @param model Input.
     * @return New model with combined faces.
     */
    public static CCModel simplifyModel(CCModel model) {
        List<Vertex5> verts = new LinkedList<>();
        Collections.addAll(verts, model.getVertices());
        List<Vertex5> combinedVerts = simplifyModel(verts);
        CCModel outModel = CCModel.quadModel(combinedVerts.size());
        outModel.verts = combinedVerts.toArray(new Vertex5[0]);
        return outModel;
    }


    /**
     * Attempts to combine square faces.
     *
     * @param in Quads in.
     * @return Quads out.
     * @author RwTema
     */
    public static LinkedList<Vertex5> simplifyModel(List<Vertex5> in) {

        LinkedList<Face> faces = new LinkedList<>();
        Iterator<Vertex5> iter = in.iterator();
        while (iter.hasNext()) {
            Face f = Face.loadFromIterator(iter);

            faces.removeIf(f::attemptToCombine);
            faces.add(f);
        }

        LinkedList<Vertex5> out = new LinkedList<>();
        for (Face f : faces) {
            Collections.addAll(out, f.verts);
        }

        return out;
    }

    private static class Face {

        public static Face loadFromIterator(Iterator<Vertex5> iter) {

            Face f = new Face(new Vertex5[4]);
            for (int i = 0; i < 4; i++) {
                f.verts[i] = iter.next();
            }

            return f;
        }

        public Vertex5[] verts;

        public Vertex5 vec(int s) {

            return verts[s & 3];
        }

        public void setVec(int s, Vertex5 newVec) {

            verts[s & 3] = newVec;
        }

        public Face(Vertex5... v) {

            assert v.length == 4;
            this.verts = v;
        }

        public boolean isPolygon() {

            for (int i = 0; i < 4; i++) {
                if (vec(i).vec.equalsT(vec(i + 1).vec)) {
                    return true;
                }
            }
            return false;
        }

        public Face reverse() {

            verts = new Vertex5[]{verts[3], verts[2], verts[1], verts[0]};
            return this;
        }

        public boolean attemptToCombine(Face other) {

            if (isPolygon() || other.isPolygon()) {
                return false;
            }

            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            other.reverse();
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            other.reverse();
            return false;
        }

        public boolean equalVert(Vertex5 a, Vertex5 b) {

            return a.vec.equalsT(b.vec) && a.uv.equals(b.uv);
        }

        public boolean attemptToCombineUnflipped(Face other) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (equalVert(vec(i), vec(j)) && equalVert(vec(i + 1), vec(j - 1))) {
                        Vector3 l1 = (vec(i - 1).vec.copy().subtract(vec(i).vec)).normalize();
                        Vector3 l2 = (vec(i + 2).vec.copy().subtract(vec(i + 1).vec)).normalize();

                        Vector3 l3 = (other.vec(j).vec.copy().subtract(other.vec(j + 1).vec)).normalize();
                        Vector3 l4 = (other.vec(j - 1).vec.copy().subtract(other.vec(j - 2).vec)).normalize();

                        if (l1.equalsT(l3) && l2.equalsT(l4)) {
                            setVec(i, other.vec(j + 1));
                            setVec(i + 1, other.vec(j - 2));
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}