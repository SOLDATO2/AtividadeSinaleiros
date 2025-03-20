package models;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Buffer { // buffer é a esteira que quero acessar
    private final int[] buffer;
    private int index = 0;

    private final Semaphore empty; // semaforo que demarca quanto o vetor esta vazio
    private final Semaphore full; // semaforo que demarca quanto o vetor esta cheio
    private final Semaphore mutex; // semaforo do tipo mutex

    public Buffer(int size) {
        this.buffer = new int[size];
        this.empty = new Semaphore(size); // inicializa com o tamanho do buffer para representar posições vazias
        this.full = new Semaphore(0); // inicializa zerado para representar que o buffer esta vazio
        this.mutex = new Semaphore(1); // serve para garantir exclusao mutua (só um thread pode acessar o buffer por vez)
    }

    public void produzir(int item, int id) throws InterruptedException {
        empty.acquire(); // decrementa o semaforo empty, aguarda espaço disponivel
        mutex.acquire(); // decrementa o semaforo mutex, entra na seção ciritca

        buffer[index] = item;
        System.err.println("Produtor - Thread " + id + " Produzido: " + item);
        
        index++;

        mutex.release(); // incrementa o semaforo mutex
        full.release(); // incrementa o semaforo full
    }

    public int consumir(int id) throws InterruptedException{
        full.acquire(); // decrementa o semaforo full, aguarda item disponivel
        mutex.acquire(); // decrementa o semaforo mutex, entra na seção ciritca
        index--;
        int item = buffer[index];
        System.err.println("Consumidor - Thread " + id + " Consumido: " + item);
        
        mutex.release(); // incrementa o semaforo mutex
        empty.release(); // incrementa o semaforo empty
        return item;

    }

    public static class Produtor implements Runnable{
        private final Buffer buffer;
        private final BufferMaterial bufferMaterial;
        private final Random random = new Random();
        private final int id;
        private int material = 0;
        
                public Produtor(Buffer buffer, BufferMaterial bufferMaterial, int id, int material) {
                    this.buffer = buffer;
                    this.id = id;
                    this.bufferMaterial = bufferMaterial;
                    this.material = material;
        }

        @Override
        public void run() {
            try {
                int custoProducao = 10;
                while (true) {

                    int materialObtido = bufferMaterial.coletarMaterial(id);
                    this.material += materialObtido;
                    System.err.println("Produtor - Thread " + id + " acumulou material: " + this.material);

                    if (this.material >= custoProducao) {
                        this.material -= custoProducao;
                        int item = random.nextInt(100);
                        buffer.produzir(item, id);
                        System.err.println("Produtor - Thread " + id + " produziu item: " + item + " (material restante: " + this.material + ")");
                    } else {
                        System.err.println("Produtor - Thread " + id + " não possui material suficiente para produzir");
                    }
                    Thread.sleep(random.nextInt(500));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    public static class Consumidor implements Runnable{
        private final Buffer buffer;
        private final Random random = new Random();
        private final int id;

        public Consumidor(Buffer buffer, int id) {
            this.buffer = buffer;
            this.id = id;
        }

        @Override
        public void run() {
            try{
                while(true){
                //for(int i = 0; i < 10; i++){
                    buffer.consumir(id);
                    Thread.sleep(random.nextInt(500));
                //}
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


}
