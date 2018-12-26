/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
 
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 *
 * @author Lionel
 */
public class PDFFacture {
    
    PdfPTable table;
    String dest;
    String startDay;
    String endDay;
    String recette;
    Document document = new Document(PageSize.A4.rotate(),-70,-70,50,50);
    public PDFFacture(String dest, String startDay, String endDay, String recette) throws IOException, DocumentException {
        this.dest = dest;
        this.startDay = startDay;
        this.endDay = endDay;
        this.recette = recette;
        this.table =new PdfPTable(5);
        createPdfFacture(dest);
    }
    
    public void createPdfFacture(String dest) throws IOException, DocumentException {
        File file = new File(dest);
        file.getParentFile().mkdirs();
        
        PdfWriter.getInstance(document, new FileOutputStream(dest));
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
        
        image.scaleAbsolute(509f, 130f);
        image.setAlignment(Image.ALIGN_CENTER);
        document.add(image);
        
        Paragraph paDate = new Paragraph(dateActuelle());
        paDate.setAlignment(Paragraph.ALIGN_CENTER);
        paDate.setSpacingBefore(30f);

        Paragraph paMagasin = new Paragraph(Res.config.getAdresse());
        paMagasin.setAlignment(Paragraph.ALIGN_CENTER);
        
        Paragraph paLieu = new Paragraph(Res.config.getTel());
        paLieu.setAlignment(Paragraph.ALIGN_CENTER);
        paLieu.setSpacingAfter(30f);
        
        Paragraph paListeFacture = new Paragraph("Liste des factures effectués entre :");
        paListeFacture.setAlignment(Paragraph.ALIGN_CENTER);
        
        Paragraph paIntervalle = new Paragraph(startDay + "  -  "+endDay);
        paIntervalle.setAlignment(Paragraph.ALIGN_CENTER);
        paIntervalle.setSpacingAfter(30f);
        
        Paragraph paRecette = new Paragraph("Recette totale : "+recette);
        paRecette.setAlignment(Paragraph.ALIGN_CENTER);
        paRecette.setSpacingAfter(30f);
        
        
        document.add(paDate);
        document.add(paMagasin);
        document.add(paLieu);
        document.add(paListeFacture);
        document.add(paIntervalle);
        document.add(paRecette);
        document.newPage();
        
        table.setWidths(new int[]{1,1,1,1,1});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("N° Facture"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Montant total"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Montant Net à payer"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Date"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Nom caissier(ère)"));
        table.addCell(cell);
        
    }
    
    public static byte [] imagetoByteArray(File imgFile){
        
        try {
            
            ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
            BufferedImage img=ImageIO.read(imgFile);
            ImageIO.write(img, "jpg", baos);
            baos.flush();

            String base64String=Base64.encode(baos.toByteArray());
            baos.close();

            byte[] bytearray = Base64.decode(base64String);
            
            return bytearray;
            
        } catch (Exception e) {
            return null;
        }
        
    }
    
    public String dateActuelle(){
        Date actuelle=new Date();
        DateFormat df=new SimpleDateFormat("EEEEEEEE, d MMMMMMMMMMMM yyyy\nHH:mm:ss");
        return df.format(actuelle);
    }
    
    public void addFactureRow(String numFact, String montantTotal, 
            String NAP, String date, String nomCaissier) throws BadElementException, IOException{
        
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(""+numFact));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+montantTotal));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+NAP));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+date));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+nomCaissier));
        table.addCell(cell);

    }
    public void save() throws DocumentException{
        document.add(table);
        document.close();
    }
    
}
