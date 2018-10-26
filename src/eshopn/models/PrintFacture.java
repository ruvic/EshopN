/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPrintPage;
import eshopn.entities.Facture;
import static eshopn.models.PDFProduit.imagetoByteArray;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.collections.ObservableList;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 *
 * @author hp
 */
public class PrintFacture extends Printer{
    
    
    private String template = 
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
    "\n" +
    "<body style=\"font-family: Consolas;\">\n" +
    "\n" +
    "<div style=\"text-align:center;font-size:10px;\">\n" +
    "<p>$adresse</p>\n" +
    "<p>Contribuable : $nro</p>\n" +
    "<p>RC : $rc</p>\n" +
    "</div>\n" +
    "<br />\n" +
    "<table style=\"text-align: left;font-size:11px;\">\n" +
    "    <tr>\n" +
    "        <td><b><u>Date:</u></b>&nbsp;&nbsp; $date</td>\n" +
    "    </tr>\n" +
    "    <tr>\n"+
    "       <td colspan=\"2\"><b><u>Caissier(i&egrave;re):</u></b>&nbsp;&nbsp; $gestName</td>\n" +        
    "    </tr>\n" +
    "    <tr>\n" +
    "        <td><b><u>Facture No:</u></b>&nbsp;&nbsp; $no</td>\n" +
    "    </tr>\n" +
    "    <tr>\n" +
    "        <td><b><u>Tel client:</u></b>&nbsp;&nbsp; $phone</td>\n" +
    "        <td></td>\n" +
    "    </tr>\n" +
    "    <tr>\n" +
    "        <td><b><u>Mode de paiement:</u></b>&nbsp;&nbsp; $mode</td>\n"+
    "    </tr>\n"+
    "    <tr>\n" +
    "        <td><b><u>Remise:</u></b>&nbsp;&nbsp; $remise %</td>\n" +
    "    </tr>\n" +
    "</table>\n" +
    "<br />\n" +
    "<table style=\"text-align: left;font-size:11px;\">\n" +
    "    <tr>\n" +
    "        <td><b><u>Montant total:</u></b>&nbsp;&nbsp; $total Fcfa</td>\n" +
    "    </tr>\n" +
    "    <tr>\n" +
    "        <td><b><u>Net &agrave; payer:</u></b>&nbsp;&nbsp; $NAP Fcfa</td>\n" +
    "    </tr>\n" +
    "</table>\n" +
    "\n" +
    "<br />\n" +
    "\n" +
    "<p>******************** Listes des produits ********************</p>" +
    "<br />\n" +
    "<table id=\"produits\" border=\"1\" style=\"border-collapse: collapse;\">\n" +
    "    <tr>\n" +
    "        <th><b>Code</b></th>\n" +
    "        <th style=\"width=5%;\"><b>Qte</b></th>\n" +
    "        <th><b>P.U</b></th>\n" +
    "        <th><b>S. Total</b></th>\n" +
    "    </tr>\n" +
    "    $products" +
    "</table>\n" +
    "<p>-------------------------------------------------------------------------</p>" +
    "<p style=\"font-size:10px;\">Les produits vendus et livrés ne sont ni repris ni échangés</p>" +
    "<p>-------------------------------------------------------------------------</p>" +
    "\n" +
    "</body>\n" +
    "\n" +
    "</html>";
    
    private Date date = new Date();
    
    public File print(Facture facture, ObservableList<MFact> listMFact,
                    double montant , double remise, String mode , String no) throws DocumentException, FileNotFoundException, BadElementException, IOException, PrinterException {   
        
        template = template.replace("$date", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date));
        template = template.replace("$gestName", facture.getIdCaissiere().getNomGest());
        template = template.replace("$phone", facture.getTel());
        
        String rows = "";
        
        for(MFact p : listMFact)
        {
            rows += "<tr style=\"font-size:10px;\"><td>"+p.getCodeProduit()+"</td><td>"+
                    p.getQte()+"</td><td>"+p.getPrixUnitaire()+"</td><td>"+p.getSousTotal()+"</td></tr>";
            
        }        
        
        template = template.replace("$products", rows);
        
        template = template.replace("$total", Res.formatNumber(montant));
        template = template.replace("$remise", String.valueOf(remise));
        template = template.replace("$NAP", Res.formatNumber((1 - remise/100) * montant));
        template = template.replace("$mode", mode);
        template = template.replace("$adresse", Res.config.getAdresse());
        template = template.replace("$nro", Res.config.getNro());
        template = template.replace("$rc", Res.config.getRc());
        template = template.replace("$no", no);
        
        Document document = new Document(new Rectangle(315, 850));
        document.setMargins(10,10, 0, 0);
        
        
        File file=new File(Res.config.getDossierFacturePdf()+generateName());
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        
        Image image = null;
        
        if(Res.config.getModeStockageImage()==1){
            File file2=new File(Res.config.getLogoPdfLocal());
            byte[] byteArray=imagetoByteArray(file2);
            
            if(byteArray!=null){
                image=Image.getInstance(byteArray);
            }else{
                System.out.println("C'était null");
            }
        }else{
            image = Image.getInstance(Res.config.getLogoPdf());
        }
        
        image.setSpacingBefore(150f);
        image.setSpacingAfter(150f);
        image.scaleAbsolute(170f, 44f);
        image.setAlignment(Image.ALIGN_CENTER);
        document.add(image);

        List<Element> parts = HTMLWorker.parseToList(new StringReader(template), null);
        for (int i = 0; i < parts.size(); i++) {
            Element e = (Element) parts.get(i);
            document.add(e);
        }
        
        document.close();
        
        return file;
    }
    
    public File printExisting(Facture facture, ObservableList<MFact> listMFact,
                    double montant , double remise, String mode , String no) throws DocumentException, FileNotFoundException, BadElementException, IOException, PrinterException{
        
       date = facture.getDateFac();
       return print(facture, listMFact, montant, remise, mode, no);
       
    }
    
}
