/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

/**
 *
 * @author MBOGNI RUVIC
 */
public class PrintProduitTask implements  Runnable {
    
    private List<Produit> listesGen;
    private ImageView loader;

    public PrintProduitTask(List<Produit> listGen, ImageView loaderImg) {
        this.listesGen = listGen;
        this.loader=loaderImg;
    }    
    
    @Override
    public void run() {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loader.setVisible(true);
                }
            });
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            File file=new File(Res.config.getDossierProduitsPdf()+sdf.format((new GregorianCalendar()).getTime())+".pdf");
            
            PDFProduit pdf=null;
            try {
                pdf = new PDFProduit(file.getAbsolutePath().replace("\\", "/"));
            } catch (IOException ex) {
                Logger.getLogger(PrintProduitTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(PrintProduitTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (Produit pr : listesGen) {
                try {
                    pdf.addProductRow(pr.getCodePro(),
                            pr.getNomPro(),
                            pr.getPrix(),
                            pr.getQte(),
                            pr.getIdCategorie().getNomCat(),
                            pr.getCodeFour(),
                            pr.getActif(),
                            lienAbsolueImage((new ArrayList<>(pr.getPhotoCollection())).get(0)));
                } catch (BadElementException ex) {
                    Logger.getLogger(PrintProduitTask.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PrintProduitTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                pdf.save();
            } catch (DocumentException ex) {
                Logger.getLogger(PrintProduitTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            Logger.getLogger(PrintProduitTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loader.setVisible(false);
            }
        });
    }
    
    public String lienAbsolueImage(Photo ph){
        String str=  Res.config.getDossierPhotos()
                + ph.getCodePro().getCodePro()
                + "/"+ph.getLienPhoto();
        return str;
    }
    
}
