package dhafer.tunisietelecom;

/**
 * Created by Dhafer ZAHROUNI on 16/06/2015.
 */
public class Infos {
int id;
String operator,idcell,lac,typenet,sig,longi,lati,mcc,datetime,altitude;
public Infos(){

}

    public Infos(int id, String operator, String idcell, String lac, String typenet, String sig, String longi, String lati, String mcc,String datetime,String altitude) {
        this.id=id;
        this.operator = operator;
        this.idcell = idcell;
        this.lac = lac;
        this.typenet = typenet;
        this.sig = sig;
        this.longi = longi;
        this.lati = lati;
        this.mcc=mcc;
        this.datetime=datetime;
        this.altitude=altitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdcell(String idcell) {
        this.idcell = idcell;
    }
    public void setMcc(String mcc) {
        this.mcc = mcc;
    }
    public void setLac(String lac) {
        this.lac = lac;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public void setTypenet(String typenet) {
        this.typenet = typenet;
    }

    public int getId() {
        return id;
    }

    public String getIdcell() {
        return idcell;
    }
    public String getMcc() {
        return mcc;
    }
    public String getLac() {
        return lac;
    }

    public String getLati() {
        return lati;
    }

    public String getLongi() {
        return longi;
    }

    public String getOperator() {
        return operator;
    }

    public String getSig() {
        return sig;
    }

    public String getTypenet() {
        return typenet;
    }
}
