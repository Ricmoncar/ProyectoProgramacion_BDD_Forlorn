
package com.daw.controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estadisticas {
    private Integer id;
    private String tipo;
    private Integer atk;
    private Integer def;
    private Integer hp;
    private Integer spe;
    private Integer mat;
    private Integer mdf;
    private Integer xp;
    private Integer lvl;
    
    public Estadisticas() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Integer getAtk() {
        return atk;
    }
    
    public void setAtk(Integer atk) {
        this.atk = atk;
    }
    
    public Integer getDef() {
        return def;
    }
    
    public void setDef(Integer def) {
        this.def = def;
    }
    
    public Integer getHp() {
        return hp;
    }
    
    public void setHp(Integer hp) {
        this.hp = hp;
    }
    
    public Integer getSpe() {
        return spe;
    }
    
    public void setSpe(Integer spe) {
        this.spe = spe;
    }
    
    public Integer getMat() {
        return mat;
    }
    
    public void setMat(Integer mat) {
        this.mat = mat;
    }
    
    public Integer getMdf() {
        return mdf;
    }
    
    public void setMdf(Integer mdf) {
        this.mdf = mdf;
    }
    
    public Integer getXp() {
        return xp;
    }
    
    public void setXp(Integer xp) {
        this.xp = xp;
    }
    
    public Integer getLvl() {
        return lvl;
    }
    
    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }
    
    /**
     * Crea una copia de estas estadísticas
     */
    public Estadisticas copiar() {
        Estadisticas copia = new Estadisticas();
        copia.setId(this.id);
        copia.setTipo(this.tipo);
        copia.setAtk(this.atk);
        copia.setDef(this.def);
        copia.setHp(this.hp);
        copia.setSpe(this.spe);
        copia.setMat(this.mat);
        copia.setMdf(this.mdf);
        copia.setXp(this.xp);
        copia.setLvl(this.lvl);
        return copia;
    }
    
    /**
     * Aplica un buff parseado desde un string como "+5 ATK, +3 DEF, -2 HP"
     */
    public void aplicarBuff(String bufoEstadisticas) {
        if (bufoEstadisticas == null || bufoEstadisticas.trim().isEmpty()) {
            return;
        }
        
        // Patrón para encontrar modificadores como "+5 ATK", "-3 DEF", etc.
        Pattern pattern = Pattern.compile("([+-]?\\d+)\\s+(ATK|DEF|HP|SPE|MAT|MDF)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(bufoEstadisticas);
        
        while (matcher.find()) {
            int valor = Integer.parseInt(matcher.group(1));
            String stat = matcher.group(2).toUpperCase();
            
            switch (stat) {
                case "ATK":
                    this.atk = (this.atk != null ? this.atk : 0) + valor;
                    break;
                case "DEF":
                    this.def = (this.def != null ? this.def : 0) + valor;
                    break;
                case "HP":
                    this.hp = (this.hp != null ? this.hp : 0) + valor;
                    break;
                case "SPE":
                    this.spe = (this.spe != null ? this.spe : 0) + valor;
                    break;
                case "MAT":
                    this.mat = (this.mat != null ? this.mat : 0) + valor;
                    break;
                case "MDF":
                    this.mdf = (this.mdf != null ? this.mdf : 0) + valor;
                    break;
            }
        }
        
        // Asegurar que ninguna estadística sea negativa
        if (this.atk != null && this.atk < 0) this.atk = 0;
        if (this.def != null && this.def < 0) this.def = 0;
        if (this.hp != null && this.hp < 0) this.hp = 1; // HP mínimo de 1
        if (this.spe != null && this.spe < 0) this.spe = 0;
        if (this.mat != null && this.mat < 0) this.mat = 0;
        if (this.mdf != null && this.mdf < 0) this.mdf = 0;
    }
}