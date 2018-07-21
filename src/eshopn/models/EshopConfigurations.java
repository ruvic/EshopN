/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.Image;

/**
 *
 * @author MBOGNI RUVIC
 */
public class EshopConfigurations {
    
    private String admin;
    private String adminPwd;
    private String adresse;
    private String tel;
    
    private String url;
    private String user;
    private String driver="com.mysql.jdbc.Driver";
    private String password;
    private String dossierApp;
    private String dossierPhotos;
    private String dossierLogos;
    private String status;
    private String uploadPhp;
    private Image logoApp;
    private URL logoPdf;
    private URL logoConnexion;
    private String dossierPhotsRelative;
    private double remise;
    private String nro;
    private String rc;
    private String dossierFacturePdf;
    private String dossierProduitsPdf;
    private String dossierStocksPdf;
    

    public EshopConfigurations(String admin, String adminPwd,
            String adresse,String tel, 
            String bd,String server, 
            String port ,String user, 
            String password, String dossierApp,
            String dossierPhotos, String dossierLogos,
            String requestController,String logoApp,
            String logoPdf, String logoConn,double remise,
            String nro, String rc,
            String factures, String produits, String stocks) throws IOException {
        this.admin = admin;
        this.adminPwd = adminPwd;
        this.adresse=adresse;
        this.tel=tel;
        this.url = "jdbc:mysql://"+server.substring(7)+":3306/"+bd+"?zeroDateTimeBehavior=convertToNull";
        this.user = user;
        this.password = password;
        this.dossierPhotos=server+":"+port+"/"+dossierApp+"/"+dossierPhotos+"/";
        this.dossierLogos=server+":"+port+"/"+dossierApp+"/"+dossierLogos+"/";
        this.dossierPhotsRelative=dossierPhotos+"/";
        this.uploadPhp=server+":"+port+"/"+dossierApp+"/"+requestController;
        
        URL url1 = new URL(server+":"+port+"/"+dossierApp+"/"+dossierLogos+"/"+logoApp);
        URL url2 = new URL(server+":"+port+"/"+dossierApp+"/"+dossierLogos+"/"+logoPdf);
        URL url3 = new URL(server+":"+port+"/"+dossierApp+"/"+dossierLogos+"/"+logoConn);
        InputStream is = url1.openStream();
        
        this.logoApp=new Image(is);
        this.logoPdf=url2;
        this.logoConnexion=url3;
        
        this.remise=remise;
        this.nro=nro;
        this.rc=rc;
        
        this.dossierFacturePdf=factures+"/";
        this.dossierProduitsPdf=produits+"/";
        this.dossierStocksPdf=stocks+"/";
        
        is.close();
    }

    /**
     * @return the admin
     */
    public String getAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(String admin) {
        this.admin = admin;
    }

    /**
     * @return the adminPwd
     */
    public String getAdminPwd() {
        return adminPwd;
    }

    /**
     * @param adminPwd the adminPwd to set
     */
    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the dossierPhotos
     */
    public String getDossierPhotos() {
        return dossierPhotos;
    }

    /**
     * @param dossierPhotos the dossierPhotos to set
     */
    public void setDossierPhotos(String dossierPhotos) {
        this.dossierPhotos = dossierPhotos;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public Image getLogoApp() {
        return logoApp;
    }

    public URL getLogoPdf() {
        return logoPdf;
    }

    public void setLogoApp(Image logoApp) {
        this.logoApp = logoApp;
    }

    public void setLogoPdf(URL logoPdf) {
        this.logoPdf = logoPdf;
    }

    

    public String getUploadPhp() {
        return uploadPhp;
    }

    public void setUploadPhp(String uploadPhp) {
        this.uploadPhp = uploadPhp;
    }

    public String getDossierPhotsRelative() {
        return dossierPhotsRelative;
    }

    public void setDossierPhotsRelative(String dossierPhotsRelative) {
        this.dossierPhotsRelative = dossierPhotsRelative;
    }

    /**
     * @return the remise
     */
    public double getRemise() {
        return remise;
    }

    /**
     * @param remise the remise to set
     */
    public void setRemise(double remise) {
        this.remise = remise;
    }

    /**
     * @return the dossierFacturePdf
     */
    public String getDossierFacturePdf() {
        return dossierFacturePdf;
    }

    /**
     * @param dossierFacturePdf the dossierFacturePdf to set
     */
    public void setDossierFacturePdf(String dossierFacturePdf) {
        this.dossierFacturePdf = dossierFacturePdf;
    }

    /**
     * @return the dossierProduitsPdf
     */
    public String getDossierProduitsPdf() {
        return dossierProduitsPdf;
    }

    /**
     * @param dossierProduitsPdf the dossierProduitsPdf to set
     */
    public void setDossierProduitsPdf(String dossierProduitsPdf) {
        this.dossierProduitsPdf = dossierProduitsPdf;
    }

    /**
     * @return the dossierStocksPdf
     */
    public String getDossierStocksPdf() {
        return dossierStocksPdf;
    }

    /**
     * @param dossierStocksPdf the dossierStocksPdf to set
     */
    public void setDossierStocksPdf(String dossierStocksPdf) {
        this.dossierStocksPdf = dossierStocksPdf;
    }

    /**
     * @return the adresse
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * @param adresse the adresse to set
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /**
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * @param tel the tel to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * @return the logoConnexion
     */
    public URL getLogoConnexion() {
        return logoConnexion;
    }

    /**
     * @param logoConnexion the logoConnexion to set
     */
    public void setLogoConnexion(URL logoConnexion) {
        this.logoConnexion = logoConnexion;
    }

    /**
     * @return the dossierLogos
     */
    public String getDossierLogos() {
        return dossierLogos;
    }

    /**
     * @param dossierLogos the dossierLogos to set
     */
    public void setDossierLogos(String dossierLogos) {
        this.dossierLogos = dossierLogos;
    }

    /**
     * @return the dossierApp
     */
    public String getDossierApp() {
        return dossierApp;
    }

    /**
     * @param dossierApp the dossierApp to set
     */
    public void setDossierApp(String dossierApp) {
        this.dossierApp = dossierApp;
    }

    /**
     * @return the nro
     */
    public String getNro() {
        return nro;
    }

    /**
     * @param nro the nro to set
     */
    public void setNro(String nro) {
        this.nro = nro;
    }

    /**
     * @return the rc
     */
    public String getRc() {
        return rc;
    }

    /**
     * @param rc the rc to set
     */
    public void setRc(String rc) {
        this.rc = rc;
    }
    
    
    
    
    
    
    
    
    
    
}
