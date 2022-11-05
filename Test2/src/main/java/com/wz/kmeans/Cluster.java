package com.wz.kmeans;

import java.util.ArrayList;
import java.util.List;


public class Cluster {

    private List<Point> points = new ArrayList<Point>(); // 属于该分类的点集
    private Point centroid; // 该分类的中心质点
    private boolean isConvergence = false;

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    @Override
    public String toString() {
        return centroid.toString();
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }


    public void initPoint() {
        points.clear();
    }

    public boolean isConvergence() {
        return isConvergence;
    }

    public void setConvergence(boolean convergence) {
        isConvergence = convergence;
    }
}
