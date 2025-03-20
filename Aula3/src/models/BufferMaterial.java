package models;

import java.util.concurrent.Semaphore;

public class BufferMaterial{
    
    private final int[] buffer;
    private int index = 0;

    private final Semaphore empty; // semaforo que demarca quanto o vetor esta vazio
    private final Semaphore full; // semaforo que demarca quanto o vetor esta cheio
    private final Semaphore mutex; // semaforo do tipo mutex

    public BufferMaterial(int size){
        this.buffer = new int[size];
        this.empty = new Semaphore(size); // inicializa com o tamanho do buffer para representar posições vazias
        this.full = new Semaphore(0); // inicializa zerado para representar que o buffer esta vazio
        this.mutex = new Semaphore(1); // serve para garantir exclusao mutua (só um thread pode acessar o buffer por vez)
    }

    public void abastecer(int item, int id) throws InterruptedException{
        empty.acquire();
        mutex.acquire();

        buffer[index] = item;
        System.err.println("Abastecedor - Thread " + id + " abasteceu: " + item);
        index++;

        mutex.release(); // incrementa o semaforo mutex
        full.release(); // incrementa o semaforo full

    }

    public int coletarMaterial(int id) throws InterruptedException{
        full.acquire();
        mutex.acquire();

        index--;
        int item = buffer[index];
        buffer[index] = buffer[index] - 10;
        System.err.println("Produtor - Thread " + id + " Coletando material: " + item);
        
        mutex.release(); // incrementa o semaforo mutex
        empty.release(); // incrementa o semaforo empty
        return item;

    }

    public static class Abastecedor implements Runnable{

        private final BufferMaterial bufferMaterial;
        private final int id;

        public Abastecedor(BufferMaterial bufferMaterial, int id){
            this.bufferMaterial = bufferMaterial;
            this.id = id;
        }



        @Override
        public void run(){

            try{
                for(int i = 0; i < 3; i++){
                //while(true){
                    int item = 10;
                    bufferMaterial.abastecer(item, id);
                    Thread.sleep(500);
                //}
                }
                System.out.println("Abastecedor " + id + "fechou !");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        }
    }

}
