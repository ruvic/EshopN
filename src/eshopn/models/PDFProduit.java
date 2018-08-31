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
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 *
 * @author Lionel
 */
public class PDFProduit {
    
    PdfPTable table;
    String dest;
    Document document = new Document(PageSize.A4.rotate(),-70,-70,50,50);
    public PDFProduit(String dest) throws IOException, DocumentException {
        this.dest = dest;
        this.table =new PdfPTable(7);
        createPdfProduct(dest);
    }
    public void createPdfProduct(String dest) throws IOException, DocumentException {
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
        
        document.add(paDate);
        document.add(paMagasin);
        document.add(paLieu);
        document.newPage();
        
        paLieu.setAlignment(Paragraph.ALIGN_CENTER);
        paDate.setSpacingAfter(30f);
        
        table.setWidths(new int[]{1,2,2,1,1,1,1});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Code produit"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Nom"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Code fournisseur"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Quantité"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Prix"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Catégorie"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Photo"));
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
    
    public void addProductRow(Integer codePro, String nomPro, BigDecimal prix,
            int qte, String categorie, String codeFour, boolean actif,
            String imgPath, boolean isAbsolutePath) throws BadElementException, IOException{
        
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(Res.formatCode(""+codePro)));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+nomPro));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+codeFour));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+qte));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+prix));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+categorie));
        table.addCell(cell);
        
        if(isAbsolutePath){
            File file=new File(imgPath);
            byte[] byteArray=imagetoByteArray(file);
            
            if(byteArray!=null){
                cell.setImage(Image.getInstance(byteArray));
            }else{
                System.out.println("C'était null");
            }
        }else{
            URL url = new URL(imgPath);
            cell.setImage(Image.getInstance(url));
        }
        
        table.addCell(cell);

    }
    public void save() throws DocumentException{
        document.add(table);
        document.close();
    }
    
}
