package com.example.innomedicapp.thread;

public class GPSTrackerThread extends Thread {

    public boolean background = true;
    @Override
    public void run() {

        while(this.background) {

            try {


                Thread.sleep(30000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }




        }

    }
}
