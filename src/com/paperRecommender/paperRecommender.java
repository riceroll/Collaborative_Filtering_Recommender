package com.paperRecommender;

import java.io.*;
import java.text.NumberFormat;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class paperRecommender {
    public static void main (String[] args){
        try {
//==========================paths of inFile & outFile===========================
            String inFileName="/Users/Roll/Desktop/hex.txt";
            String outFileName="/Users/Roll/Desktop/result.txt";
//==============================================================================
//          temporary files
            String inFileNameDec="/Users/Roll/Desktop/dec.txt";
            String outFileNameDec="/Users/Roll/Desktop/result_dec.txt";

//          convert inFile from Hex to Dec
            Hex2Dec(inFileName,inFileNameDec);

//          load files and writer
            File inFileDec=new File(inFileNameDec);
            File outFileDec=new File(outFileNameDec);
            FileWriter fileWriter=new FileWriter(outFileDec);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

//          Collaborative Filtering
            DataModel dm = new FileDataModel(inFileDec);
            TanimotoCoefficientSimilarity sim = new TanimotoCoefficientSimilarity(dm);
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dm, sim);

//          write recommends into outFile
            int counter=1;
            double tmp1;
            double tmp2=0;
            NumberFormat num = NumberFormat.getPercentInstance();
            num.setMaximumIntegerDigits(3);
            num.setMaximumFractionDigits(0);
            for(LongPrimitiveIterator items = dm.getItemIDs(); items.hasNext();) {  //recommends for every user paper
                tmp1=(double)counter/dm.getNumItems();
                if ((tmp1-tmp2)>0.01) {
                    tmp2=tmp1;
                    System.out.println(num.format(tmp2));
                }
                long itemId = items.nextLong();
                List<RecommendedItem>recommendations = recommender.mostSimilarItems(itemId, 5);
                for(RecommendedItem recommendation : recommendations) {     //recommends for every item paper
                    String s=new String(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue()+"\r\n");
                    bufferedWriter.write(s);
                }
                counter++;
            }
            System.out.println("100%\r\nSuccessfully completed!");
            bufferedWriter.close();

//          convert outFile from Dec to Hex
            Dec2Hex(outFileNameDec,outFileName);

//          delete temporary files
            inFileDec.delete();
            outFileDec.delete();

        } catch (IOException e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        } catch (TasteException e) {
            System.out.println("There was a Taste Exception");
            e.printStackTrace();
        }
    }

    private static void Hex2Dec(String inFileName,String inFileNameDec) {
        try {
            File inFile=new File(inFileName);
            File outFile=new File(inFileNameDec);

            BufferedReader inFileBufferedReader= new BufferedReader(new FileReader(inFile));
            BufferedWriter outFileBufferedWriter= new BufferedWriter(new FileWriter(outFile));

            String line;
            while (inFileBufferedReader.ready()) {
                line = inFileBufferedReader.readLine();

                String hexItems[] = line.split(",");
                String decItems[] = new String[3];
                decItems[0] = String.valueOf(Integer.parseInt(hexItems[0], 16));
                decItems[1] = String.valueOf(Integer.parseInt(hexItems[1], 16));

                outFileBufferedWriter.write(decItems[0] + ',' + decItems[1] + ',' + hexItems[2]+'\n');
            }
            outFileBufferedWriter.close();

        } catch (IOException e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }
    }

    private static void Dec2Hex(String outFileNameDec,String outFileName) {
        try {
            File inFile=new File(outFileNameDec);
            File outFile=new File(outFileName);

            BufferedReader inFileBufferedReader= new BufferedReader(new FileReader(inFile));
            BufferedWriter outFileBufferedWriter= new BufferedWriter(new FileWriter(outFile));

            String line;
            while (inFileBufferedReader.ready()) {
                line = inFileBufferedReader.readLine();
                System.out.println(line);

                String decItems[] = line.split(",");
                String hexItems[] = new String[3];
                hexItems[0] = String.valueOf(Integer.toHexString(Integer.parseInt(decItems[0])));
                hexItems[1] = String.valueOf(Integer.toHexString(Integer.parseInt(decItems[1])));

                outFileBufferedWriter.write(hexItems[0] + ',' + hexItems[1] + ',' + decItems[2]+'\n');
            }
            outFileBufferedWriter.close();

        } catch (IOException e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }
    }
}
