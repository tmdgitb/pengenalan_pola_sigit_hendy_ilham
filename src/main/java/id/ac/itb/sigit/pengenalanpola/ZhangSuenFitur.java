package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 18/10/2015.
 */
public class ZhangSuenFitur {
    private int loopCount = 0;
    private List<ZhangSuenEdge> edges = new ArrayList<>();
    private List<ZhangSuenCross> crosses = new ArrayList<>();

    public int getLoopCount() {
        return loopCount;
    }

    public String getLoopString() {
        return String.valueOf(loopCount);
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public List<ZhangSuenEdge> getEdges() {
        return edges;
    }

    public String getEdgesString() {
        if (edges == null) {
            return "";
        }
        String msg = String.valueOf(edges.size()) + "==> ";
        for (int i = 0; i < edges.size(); i++) {
            msg = msg + "; point x : " + String.valueOf(edges.get(i).getEdge().getX())
                    + " ,point y : " + String.valueOf(edges.get(i).getEdge().getY());
        }

        return msg;
    }

    public List<ZhangSuenCross> getCrosses() {
        return crosses;
    }

    public String getCrossesString() {
        if (crosses == null) {
            return "";
        }
        String msg = String.valueOf(crosses.size()) + "==> ";
        for (int i = 0; i < crosses.size(); i++) {
            msg = msg + "; point x : " + String.valueOf(crosses.get(i).getEdge().getX())
                    + " ,point y : " + String.valueOf(crosses.get(i).getEdge().getY());
        }

        return msg;
    }

}
