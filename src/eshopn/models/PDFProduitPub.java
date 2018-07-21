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

/**
 *
 * @author Lionel
 */
public class PDFProduitPub {
    
    PdfPTable table;
    String dest;
    Document document = new Document(PageSize.A4.rotate(),-70,-70,50,50);
    public PDFProduitPub(String dest) throws IOException, DocumentException {
        this.dest = dest;
        this.table =new PdfPTable(4);
        createPdfProduct(dest);
    }
    public void createPdfProduct(String dest) throws IOException, DocumentException {
        File file = new File(dest);
        file.getParentFile().mkdirs();
        
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        
        Image image = Image.getInstance(Res.config.getLogoPdf());
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
        
        table.setWidths(new int[]{2,2,2,1});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Code produit"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Nom"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Prix"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Photo"));
        table.addCell(cell);
        
    }
    
    public String dateActuelle(){
        Date actuelle=new Date();
        DateFormat df=new SimpleDateFormat("EEEEEEEE, d MMMMMMMMMMMM yyyy\nHH:mm:ss");
        return df.format(actuelle);
    }
    
    public void addProductRow(Integer codePro, String nomPro, BigDecimal prix, int qte, String categorie, String codeFour, boolean actif,String img) throws BadElementException, IOException{
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase(Res.formatCode(""+codePro)));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+nomPro));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+prix));
        table.addCell(cell);
        
        URL url = new URL(img);
        cell.setImage(Image.getInstance(url));
        table.addCell(cell);

    }
    public void save() throws DocumentException{
        document.add(table);
        document.close();
    }
    
}
