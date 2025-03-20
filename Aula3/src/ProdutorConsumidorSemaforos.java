import java.util.Vector;

import models.Buffer;
import models.BufferMaterial;
import models.Buffer.Consumidor;
import models.Buffer.Produtor;
import models.BufferMaterial.Abastecedor;


public class ProdutorConsumidorSemaforos {
    public static void main(String[] args) throws Exception {
        //1. semaforo é similar a passagem de token, quem tem o token do semaforo, pode continuar executando 
        // (mutex) é um semaforo binario, ou seja, só pode ter 0 ou 1, o semaforo possui 1 posição para emprestar a um thread

        //2. Existe outro tipo de semaforo que é o semaforo de contagem, que pode ter mais de 1 token,
        // e a quantidade de tokens é definida pelo programador. Esse semaforo possui varias posições para emprestar
        // a varios threads

        // nome da classe é Semaphore. se no construtor passar 1 = significa que é mutex, qualuqer coisa
        // maior que 1 significa que possui mais tokens

        Buffer buffer = new Buffer(5);
        BufferMaterial bufferMaterial = new BufferMaterial(3);

        Vector <Thread> threads_abastecedores = new Vector<Thread>();
        for(int i = 0; i < 2; i++){
            threads_abastecedores.add(new Thread(new Abastecedor(bufferMaterial, i)));
        }
        Vector <Thread> threads_produtores = new Vector<Thread>();
        for (int i = 0; i < 5; i++) {
            threads_produtores.add(new Thread(new Produtor(buffer, bufferMaterial, i, 0)));
        }
        Vector <Thread> threads_consumidores = new Vector<Thread>();
        for (int i = 0; i < 8; i++) {
            threads_consumidores.add(new Thread(new Consumidor(buffer, i)));
        }

        //Thread produtor = new Thread(new Produtor(buffer));
        //Thread consumidor = new Thread(new Consumidor(buffer));

        //produtor.start();
        //consumidor.start();

        for (Thread thread : threads_abastecedores){
            thread.start();
        }
        
        for (Thread thread : threads_produtores) {
            thread.start();
        }
        for (Thread thread : threads_consumidores) {
            thread.start();
        }

        try{
            //produtor.join();
            //consumidor.join(); // join garante que thread finalizou  
            // tecnicamente eles nunca irão finalizar pois as threads ficam em loop infinito no metodo run
            for (Thread thread : threads_produtores){
                thread.join();
            }
            System.out.println("Abastecedores finalizados");
            for (Thread thread : threads_produtores) {
                thread.join();
            }
            System.out.println("Produtores finalizados");
            for (Thread thread : threads_consumidores) {
                thread.join();
            }
            System.out.println("Consumidores finalizados");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Fim da execução");
    }



    //desafio 5 threads produtoras e 8 consumidoras
    //material gerado de tempo em tempo e armazenado em um buffer
    //threads produtoras consomem o material gerado


}
