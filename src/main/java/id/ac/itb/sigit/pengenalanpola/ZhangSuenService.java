package id.ac.itb.sigit.pengenalanpola;

import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 16/10/2015.
 */
public class ZhangSuenService {

    private ZhangSuen zhangSuen;
    private static final Logger log = LoggerFactory.getLogger(ChainCodeService.class);
    private opencv_core.Mat origMat;
    private opencv_core.Mat grayOrigMat;
    private opencv_core.Mat zhangSuenMat;
    private boolean flag[][];
    private List<ZhangSuenFitur> zhangSuenFiturList;

    public opencv_core.Mat loadInput(File imageFile)
    {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath());
        grayOrigMat =opencv_highgui.imread(imageFile.getPath(), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        final ByteIndexer idx = grayOrigMat.createIndexer();
        zhangSuen=new ZhangSuen();
        zhangSuenMat = zhangSuen.process(grayOrigMat);
        getZhangSuenFitur();
        return origMat;
    }

    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes) {
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        origMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_UNCHANGED);
        grayOrigMat =opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
        final ByteIndexer idx = grayOrigMat.createIndexer();
        zhangSuen=new ZhangSuen();
        zhangSuenMat = zhangSuen.process(grayOrigMat);
        getZhangSuenFitur();
        return origMat;
    }

    public opencv_core.Mat getOrigMat()
    {
        return origMat;
    }

    public opencv_core.Mat getZhainSuenMat()
    {
        return zhangSuenMat;
    }


    private List<ZhangSuenFitur> getZhangSuenFitur()
    {
        flag=new boolean[ zhangSuenMat.cols()][zhangSuenMat.rows()];
        zhangSuenFiturList=new ArrayList<>();
        final ByteIndexer imgIdx = zhangSuenMat.createIndexer();

        for (int x = 0; x < zhangSuenMat.cols(); x++) {
            for (int y = 0; y < zhangSuenMat.rows(); y++) {
                int pxl = Byte.toUnsignedInt(imgIdx.get(x, y));
                if(pxl>250 && ! flag[x][y])
                {
                    ZhangSuenFitur zhangSuenFitur=new ZhangSuenFitur();
                    zhangSuenFitur= prosesZhangSuenFitur(x,y, imgIdx, zhangSuenFitur);
                    zhangSuenFiturList.add(zhangSuenFitur);
                    log.info("Chaincode object #{} at ({}, {}): {}", zhangSuenFitur, x, y, zhangSuenFitur.getSimpangan());
                }
            }
        }

        return zhangSuenFiturList;
    }


    private ZhangSuenFitur prosesZhangSuenFitur(int col,int row,ByteIndexer idxImg,ZhangSuenFitur zhangSuenFitur)
    {
        if(flag[col][row])
        {
            //buletan
            zhangSuenFitur.setBulatan(zhangSuenFitur.getBulatan()+1);
            return zhangSuenFitur;
        }

        flag[col][row]=true;

        if((col - 1)<0 || (row - 1)<0 ||
        (col+1) >= idxImg.cols()|| (row+1)>=idxImg.rows())
        {
            return zhangSuenFitur;
        }

        List<Edge> dataTetangga=new ArrayList<>();
        Edge edge1=new Edge(col - 1, row - 1);
        edge1.setvalue(idxImg.get(col - 1, row - 1));
        dataTetangga.add(edge1);

        Edge edge2=new Edge(col-1,row);
        edge2.setvalue(idxImg.get(col-1,row));
        dataTetangga.add(edge2);

        Edge edge3=new Edge(col-1,row+1);
        edge3.setvalue(idxImg.get(col-1,row+1));
        dataTetangga.add(edge3);

        Edge edge4=new Edge(col,row+1);
        edge4.setvalue(idxImg.get(col,row+1));
        dataTetangga.add(edge4);

        Edge edge5=new Edge(col+1,row+1);
        edge5.setvalue(idxImg.get(col+1,row+1));
        dataTetangga.add(edge5);

        Edge edge6=new Edge(col+1,row);
        edge6.setvalue(idxImg.get(col+1,row));
        dataTetangga.add(edge6);

        Edge edge7=new Edge(col+1,row-1);
        edge7.setvalue(idxImg.get(col+1,row-1));
        dataTetangga.add(edge7);

        Edge edge8=new Edge(col,row-1);
        edge8.setvalue(idxImg.get(col,row-1));
        dataTetangga.add(edge8);

        List<Edge> nextStep=new ArrayList<>();

        int temppotition=-1;

        boolean ujung=false;
        boolean cabang=true;

        for(int i=0;i<dataTetangga.size();i++)
        {
            if(dataTetangga.get(i).getvalue()!=0)
            {
                if(temppotition<0)
                {
                    ujung=true;
                    nextStep.add(dataTetangga.get(i));
                    temppotition=i;
                }
                else {
                    ujung=false;
                    nextStep.add(dataTetangga.get(i));
                    int cek = i - temppotition;
                    if (cek < 2) {
                        cabang = false;
                    }
                    temppotition = i;
                }
            }
        }
        if(nextStep.size()==0)
        {
            //cuma 1 pixel;
            return zhangSuenFitur;
        }
        else if(ujung && nextStep.size()<2)
        {
            //ujung
            ZhangSuenUjung zhangSuenUjung=new ZhangSuenUjung();
            zhangSuenUjung.setEdge(nextStep.get(0));
            zhangSuenFitur.getUjung().add(zhangSuenUjung);
            return zhangSuenFitur;
        }
        else if (cabang && nextStep.size()>2)
        {
            //cabang

            ZhangSuenSimpangan zhangSuenSimpangan = new ZhangSuenSimpangan();
            zhangSuenSimpangan.setEdge(new Edge(col,row));
            zhangSuenSimpangan.getPoints().addAll(nextStep);
            zhangSuenFitur.getSimpangan().add(zhangSuenSimpangan);
            for(int i=0;i<nextStep.size();i++)
            {
                if(!flag[nextStep.get(i).getX()][nextStep.get(i).getY()]) {
                    zhangSuenFitur = prosesZhangSuenFitur(nextStep.get(i).getX(), nextStep.get(i).getY(), idxImg, zhangSuenFitur);
                }
            }
            return  zhangSuenFitur;
        }
        else
        {
            for(int i=0;i<nextStep.size();i++)
            {
                if(!flag[nextStep.get(i).getX()][nextStep.get(i).getY()]) {
                    zhangSuenFitur = prosesZhangSuenFitur(nextStep.get(i).getX(), nextStep.get(i).getY(), idxImg, zhangSuenFitur);
                }
            }
            return zhangSuenFitur;
        }
    }
}
