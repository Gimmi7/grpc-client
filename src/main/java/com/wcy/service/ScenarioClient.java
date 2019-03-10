package com.wcy.service;

import com.wcy.scenario.*;
import com.wcy.util.FileChannelWriter;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Service
public class ScenarioClient {
    private ManagedChannel channel;
    private ScenarioGrpc.ScenarioBlockingStub blockingStub;
    private ScenarioGrpc.ScenarioStub asyncstub;
    private ScenarioGrpc.ScenarioFutureStub futureStub;

    public void initialize(String host, int port){
        channel= ManagedChannelBuilder.forAddress(host,port).usePlaintext().build();
        blockingStub=ScenarioGrpc.newBlockingStub(channel);
        asyncstub=ScenarioGrpc.newStub(channel);
        futureStub=ScenarioGrpc.newFutureStub(channel);
    }

    public void shutdown() throws InterruptedException {
        System.out.println("grpc-client will be shutdown after 5 seconds");
        channel.awaitTermination(5,TimeUnit.SECONDS);
    }

    public void blockingUtilShutdown() throws InterruptedException {
        channel.awaitTermination(10,TimeUnit.DAYS);
    }

    public void cheekIn(){
       StreamObserver<Empty> responseObserver=new StreamObserver<Empty>() {
           @Override
           public void onNext(Empty empty) {

               System.out.println("cheekIn onNext");
           }

           @Override
           public void onError(Throwable throwable) {
               System.err.println("cheekIn error");
           }

           @Override
           public void onCompleted() {
               System.out.println("cheekIn completed");
           }
       };

        StreamObserver<Person> requestObserver=asyncstub.cheekIn(responseObserver);
        for (int i = 1; i <= 3; i++) {
            Person p=Person.newBuilder().setName("person"+i).build();
            requestObserver.onNext(p);
        }
        requestObserver.onCompleted();
    }

    public void chunker() throws FileNotFoundException {
        StreamObserver<Chunk> responseObserver=new StreamObserver<Chunk>() {

            FileChannelWriter writer=new FileChannelWriter("/root/视频/concat.mp4");

            @Override
            public void onNext(Chunk chunk) {
                byte[] array=chunk.getChunk().toByteArray();
                try {
                    long writeSize = writer.write(array);
                    System.out.println(writeSize+" 字节写入文件concat.mp4");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {

                System.err.println("chunker() error");
            }

            @Override
            public void onCompleted() {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        asyncstub.chunker(Empty.newBuilder().build(), responseObserver);
    }

    public void getCompanies(String companyName){
        StreamObserver<Company> responseObserver=new StreamObserver<Company>() {
            @Override
            public void onNext(Company company) {
                System.out.println(company);
            }

            @Override
            public void onError(Throwable throwable) {

                System.err.println("getCompanies error");
            }

            @Override
            public void onCompleted() {

                System.out.println("getCompanies error");
            }
        };

        CompanyName request=CompanyName.newBuilder().setCompanyName(companyName).build();
        asyncstub.withDeadlineAfter(4, TimeUnit.SECONDS).getCompanies(request,responseObserver);

        /**
         * 下面是使用blockingStub的情况
         */
//        Iterator<Company> iterator=blockingStub
//                .getCompanies(CompanyName.newBuilder().setCompanyName(companyName).build());
//        while (iterator.hasNext()){
//            System.out.println(iterator.next());
//        }
    }

    public void batchWriteCompany(){
        StreamObserver<Empty> responseObserver=new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty empty) {
                System.out.println("batchWrite next");
            }

            @Override
            public void onError(Throwable throwable) {

                System.err.println("batchWrite error");
            }

            @Override
            public void onCompleted() {

                System.out.println("batchWrite completed");
            }
        };
        StreamObserver<Company> requestObserver=asyncstub.batchWriteCompanies(responseObserver);
        Company company1=Company.newBuilder().setName("companyA").setStoredIn("dynamodb").build();
        requestObserver.onNext(company1);
        Company company2=Company.newBuilder().setName("companyB").setStoredIn("dynamodb").build();
        requestObserver.onNext(company2);
        Company company3=Company.newBuilder().setName("companyC").setStoredIn("dynamodb").build();
        requestObserver.onNext(company3);
        requestObserver.onCompleted();
    }

    public void translate(){
        StreamObserver<Sentence> responseObserver=new StreamObserver<Sentence>() {
            @Override
            public void onNext(Sentence sentence) {
                System.out.println(sentence.getSentence());
            }

            @Override
            public void onError(Throwable throwable) {

                System.err.println("translate error");
            }

            @Override
            public void onCompleted() {

                System.out.println("translate completed");
            }
        };

        StreamObserver<Sentence> requestObserver=asyncstub.translate(responseObserver);
        Sentence sen1=Sentence.newBuilder().setSentence("你好").build();
        requestObserver.onNext(sen1);
        System.out.println(sen1.getSentence());

        Sentence sen2=Sentence.newBuilder().setSentence("今天是星期三").build();
        requestObserver.onNext(sen2);
        System.out.println(sen2.getSentence());

        Sentence sen3=Sentence.newBuilder().setSentence("再见").build();
        requestObserver.onNext(sen3);
        System.out.println(sen3.getSentence());
    }
}
