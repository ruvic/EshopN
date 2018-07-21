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
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author Lionel
 */
public class PDFStock {
    
    PdfPTable table;
    String dest;
    Document document = new Document(PageSize.A4.rotate(),-70,-70,50,50);
    public PDFStock(String dest) throws IOException, DocumentException {
        this.dest = dest;
        this.table =new PdfPTable(6);
        createPdfStock(dest);
    }
    public void createPdfStock(String dest) throws IOException, DocumentException {
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
        
        table.setWidths(new int[]{2,2,2,2,2,3});
        
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Nom du produit"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Code du produit"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Quantité"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Date Stock"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("OPération"));
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Photo"));
        table.addCell(cell);
        
        
    }
    
    public String dateActuelle(){
        Date actuelle=new Date();
        DateFormat df=new SimpleDateFormat("EEEEEEEE, d MMMMMMMMMMMM yyyy\nHH:mm:ss");
        return df.format(actuelle);
    }
    
    public void addStockRow(String nomProduit,String codeProduit, int qte, Date dateStock, boolean operation,String img) throws BadElementException, IOException{
        PdfPCell cell = new PdfPCell();
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'à' hh:mm:ss a zzz");
        cell.setPhrase(new Phrase(""+nomProduit));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+codeProduit));
        table.addCell(cell);
        String operationstring = operation?"ajout":"retrait";
        cell.setPhrase(new Phrase(""+qte));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+Res.sdf.format(dateStock)));
        table.addCell(cell);
        cell.setPhrase(new Phrase(""+operationstring));
        table.addCell(cell);

        URL url = new URL(img);
        InputStream is = url.openStream();
        cell.setImage(Image.getInstance(url));
        is.close();
  
        table.addCell(cell);
    

    }
    public void save() throws DocumentException{
        document.add(table);
        document.close();
    }
    
}
