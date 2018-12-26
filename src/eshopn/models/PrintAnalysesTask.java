/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import eshopn.entities.Facture;
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
public class PrintAnalysesTask implements  Runnable {
    
    private List<MFacture> listFact;
    private ImageView loader;
    private String startDay;
    private String endDay;
    private String recette;

    public PrintAnalysesTask(List<MFacture> listFact, String startDay, 
            String endDay, String recette,ImageView loaderImg) {
        this.listFact = listFact;
        this.loader=loaderImg;
        this.startDay = startDay;
        this.endDay = endDay;
        this.recette = recette;
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
            
            PDFFacture pdf=null;
            try {
                pdf = new PDFFacture(file.getAbsolutePath().replace("\\", "/"), startDay,
                        endDay, recette);
            } catch (IOException ex) {
                Logger.getLogger(PrintAnalysesTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(PrintAnalysesTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (MFacture mfact : listFact) {
                try {
                    
                    pdf.addFactureRow(
                        mfact.getNumFact(),
                        mfact.getMontant(),
                        mfact.getMontNAP(),
                        mfact.getDate(),
                        mfact.getNomCaissier()
                    );
                    
                } catch (BadElementException ex) {
                    Logger.getLogger(PrintAnalysesTask.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PrintAnalysesTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                pdf.save();
            } catch (DocumentException ex) {
                Logger.getLogger(PrintAnalysesTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            Logger.getLogger(PrintAnalysesTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loader.setVisible(false);
            }
        });
    }
    
    
}
