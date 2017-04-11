/*

 */
package com.example.craig.ssgps;



public class SettingItem {

    private int ID;
    private int checks;
    private int report;
    private int missed;

    public SettingItem(int ID, int checks, int report, int missed){
        this.ID = ID;
        this.checks = checks;
        this.report = report;
        this.missed = missed;
    }

    public int getID(){return ID;}

    public int getChecks(){return checks;}

    public int getReport(){return report;}

    public int getMissed(){return missed;}

}
