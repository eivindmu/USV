/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;



/**
 *
 * @author vegard Klasse for å mellomlagre data sendt fra USV
 */
public class DataStorage {

    private float[] data;
    private double[] latLon;
    private boolean dataUpdated = true;
    private boolean egnosEnabled=false;
    private boolean latLonUpdated = true;

    public DataStorage() {
        data = new float[23];
        latLon = new double[2];
    }

    // Sett data mottatt fra USV
    public synchronized void setArray(float[] data, double[] latLon) {
        dataUpdated = true;
        latLonUpdated = true;
        this.data = data;
        this.latLon = latLon;
    }
    
    // Hent all data bortsett fra lengde- og breddegrad
    public synchronized float[] getDataArray() {
        dataUpdated = false;
        return data;
    }
    
    // Hent lengde- og breddegrad
    public synchronized double[] getLatLonFromUSV() {
        latLonUpdated = false;
        return latLon;
    }
    
    // Sett egnos-status
    public synchronized void setEgnosEnabled(boolean enabled) {
        egnosEnabled = enabled;
    }
    
    // Hent egnos-status
    public synchronized boolean getEgnosEnabled() {
        return egnosEnabled;
    }

    // Er data oppdatert?
    public synchronized boolean updated() {
        return (dataUpdated && latLonUpdated);
    }
}
