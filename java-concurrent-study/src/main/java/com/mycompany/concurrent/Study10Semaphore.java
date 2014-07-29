
package com.mycompany.concurrent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Semaphore
 * @author tsasaki
 */
public class Study10Semaphore {
    

    private static class Task implements Runnable{
        private int width;
        private final Semaphore semaphore;
        private boolean moving;

        public Task(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                for (int c = 0; c < 10; c++){

                    semaphore.acquire();    //セマフォを獲得できるのは3つまで
                    moving = true;

                    for (int i = 0; i < 80; i++){
                        width+=1;
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                    moving = false;
                    semaphore.release();    //開放
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Study10Semaphore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Semaphore");
        MyPanel panel = new MyPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();

        Semaphore semaphore = new Semaphore(3);
        ExecutorService es = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++){
            Task dt = new Task(semaphore);
            es.submit(dt);
            panel.tasks.add(dt);
        }

        new Thread(()->{
            while(true){
                frame.repaint();
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Study10Semaphore.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }).start();

        frame.setVisible(true);
    }
    private static class MyPanel extends JComponent{
        List<Task> tasks = new ArrayList<>();

        public MyPanel() throws HeadlessException {
            setPreferredSize(new Dimension(1024, 600));
        }

        @Override
        public void paint(Graphics g) {

            int y = 0;
            for (Task dt: tasks){
                g.setColor(dt.moving ? Color.RED : Color.GRAY);
                g.fillRect(0, y, dt.width, 50);
                y+=60;
            }
        }
    }
}
