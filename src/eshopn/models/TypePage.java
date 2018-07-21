/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

/**
 *
 * @author MBOGNI RUVIC
 */
public enum TypePage {
   FIRST("FIRST"),  // première page
   ZERO("ZERO"), // 00 
   UN("UN"),   //01
   DEUX("DEUX"),  //10
   TROIS("TROIS");  //11
   
   private String name;

   private TypePage(String name) {
       this.name = name;
   }
   
   public String toString(){
       return this.name;
   }
   
   
   
}
