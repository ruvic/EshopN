/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author MBOGNI RUVIC
 */
public class PrintCatalogue extends  Printer {
    private final ArrayList<Produit> produits;
    private String destPath="";
    
    public PrintCatalogue(ArrayList<Produit> produits)
    {
        this.destPath=Res.config.getDossierProduitsPdf()+generateName();
        this.produits = produits;
    }
    
    public void printAndOpen() throws DocumentException, FileNotFoundException, BadElementException, IOException
    {
        Document document = new Document(PageSize.A4);
        document.addTitle("Catalogue des produits");
        document.setMargins(10, 10, 25, 25);
        
        PdfWriter.getInstance(document, new FileOutputStream(this.destPath));
        document.open();

        // creation de la table pour inserer les produits 
        PdfPTable table = new PdfPTable(4); // 4 columns
        table.setWidthPercentage(100);
        table.setSpacingBefore(6f);
        table.setSpacingAfter(6f);

        // definition du style des caracteres
        Font font = new Font(Font.FontFamily.UNDEFINED, 12);

        Paragraph par = new Paragraph("Catalogue du " + new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.FRENCH).format(new Date()), font);
        par.setAlignment(Paragraph.ALIGN_TOP);
        par.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(par);
        document.add(new Paragraph("   "));

        for (Produit produit : this.produits)
        {
            if(produit.getQte() <= 0) continue;
            ObservableList<Photo> photos=FXCollections.observableArrayList(produit.getPhotoCollection());
            
            URL url=new URL(lienAbsolueImage(photos.get(0)));
            Image img = Image.getInstance(url);
            img.setAlignment(Image.ALIGN_TOP);
            img.setAlignment(Image.ALIGN_CENTER);
            
            PdfPCell cell = new PdfPCell(new Phrase(Res.formatCode(produit.getCodePro().toString()), font));
            cell.addElement(new Phrase(Res.formatCode(produit.getCodePro().toString()), font));
            cell.addElement(img);
            cell.addElement(new Phrase(produit.getDescription(), font));
            
            
            table.addCell(cell);
        }
        
        
        
        document.add(table);

        document.close();

        Desktop.getDesktop().open(new File(this.destPath));
    }
    
    public String lienAbsolueImage(Photo ph){
        String str=  Res.config.getDossierPhotos()
                + ph.getCodePro().getCodePro()
                + "/"+ph.getLienPhoto();
        return str;
    }
}
