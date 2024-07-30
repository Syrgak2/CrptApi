package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {

    private final HttpClient httpClient;
    private final Semaphore rateLimiter;
    private final ScheduledExecutorService scheduler;
    private final Duration timeUnit;

    public CrptApi(int requestLimit, TimeUnit timeUnit) {
        this.httpClient = HttpClient.newHttpClient();

        this.scheduler = Executors.newScheduledThreadPool(1);
        this.timeUnit = Duration.ofMillis(timeUnit.toMillis(1));
        this.rateLimiter = new Semaphore(requestLimit);

        // Reset the rate limiter periodically
        scheduler.scheduleAtFixedRate(() -> rateLimiter.release(requestLimit),
                timeUnit.toMillis(1),
                timeUnit.toMillis(1),
                TimeUnit.MILLISECONDS);
    }

    public void createDocument(Document document, String signature) throws InterruptedException, IOException {
        rateLimiter.acquire();

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(document);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                .header("Content-Type", "application/json")
                .header("Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send the request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CrptApi crptApi = new CrptApi( 10, TimeUnit.MINUTES);

        // Example document
        Document document = new Document();
        String signature = "Test";

        crptApi.createDocument(document, signature);


    }

    public enum DocType{
        LP_INTRODUCE_GOODS
    }

    public static class Description{
        private String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Description that = (Description) o;
            return Objects.equals(participantInn, that.participantInn);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(participantInn);
        }
    }

    public static class Document {
        private String docId;
        private String docStatus;
        private DocType docType;
        private boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private LocalDate productionDate;
        private String productionType;
        private LocalDate regDate;
        private String regNumber;


        private List<Products> products;



        public Document() {
        }

        public Document(String docId, String docStatus, DocType docType,
                        boolean importRequest, String ownerInn,
                        String participantInn, String producerInn,
                        LocalDate productionDate, String productionType,
                        List<Products> products, LocalDate regDate, String regNumber) {

            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.regDate = regDate;
            this.regNumber = regNumber;
            this.products = products;
        }

        public LocalDate getRegDate() {
            return regDate;
        }

        public void setRegDate(LocalDate regDate) {
            this.regDate = regDate;
        }

        public String getRegNumber() {
            return regNumber;
        }

        public void setRegNumber(String regNumber) {
            this.regNumber = regNumber;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public DocType getDocType() {
            return docType;
        }

        public void setDocType(DocType docType) {
            this.docType = docType;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public LocalDate getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
        }

        public String getProductionType() {
            return productionType;
        }

        public void setProductionType(String productionType) {
            this.productionType = productionType;
        }

        public List<Products> getProducts() {
            return products;
        }

        public void setProducts(List<Products> products) {
            this.products = products;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Document document = (Document) o;
            return importRequest == document.importRequest && Objects.equals(docId, document.docId) && Objects.equals(docStatus, document.docStatus) && docType == document.docType && Objects.equals(ownerInn, document.ownerInn) && Objects.equals(participantInn, document.participantInn) && Objects.equals(producerInn, document.producerInn) && Objects.equals(productionDate, document.productionDate) && Objects.equals(productionType, document.productionType) && Objects.equals(regDate, document.regDate) && Objects.equals(regNumber, document.regNumber) && Objects.equals(products, document.products);
        }

        @Override
        public int hashCode() {
            return Objects.hash(docId, docStatus, docType, importRequest, ownerInn, participantInn, producerInn, productionDate, productionType, regDate, regNumber, products);
        }
    }


    private static class Products {
        private String certificateDocument;
        private LocalDate certificateDocumentDate;
        private String certificateDocumentNumber;
        private String ownerInn;
        private String producerInn;
        private LocalDate productionDate;
        private String tnvedCode;
        private String uitCode;
        private String uituCode;

        public Products() {
        }

        public Products(String certificateDocument, LocalDate certificateDocumentDate,
                        String certificateDocumentNumber, String ownerInn,
                        String producerInn, LocalDate productionDate,
                        String tnvedCode, String uitCode, String uituCode) {
            this.certificateDocument = certificateDocument;
            this.certificateDocumentDate = certificateDocumentDate;
            this.certificateDocumentNumber = certificateDocumentNumber;
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
            this.uitCode = uitCode;
            this.uituCode = uituCode;
        }

        public String getCertificateDocument() {
            return certificateDocument;
        }

        public void setCertificateDocument(String certificateDocument) {
            this.certificateDocument = certificateDocument;
        }

        public LocalDate getCertificateDocumentDate() {
            return certificateDocumentDate;
        }

        public void setCertificateDocumentDate(LocalDate certificateDocumentDate) {
            this.certificateDocumentDate = certificateDocumentDate;
        }

        public String getCertificateDocumentNumber() {
            return certificateDocumentNumber;
        }

        public void setCertificateDocumentNumber(String certificateDocumentNumber) {
            this.certificateDocumentNumber = certificateDocumentNumber;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public LocalDate getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
        }

        public String getTnvedCode() {
            return tnvedCode;
        }

        public void setTnvedCode(String tnvedCode) {
            this.tnvedCode = tnvedCode;
        }

        public String getUitCode() {
            return uitCode;
        }

        public void setUitCode(String uitCode) {
            this.uitCode = uitCode;
        }

        public String getUituCode() {
            return uituCode;
        }

        public void setUituCode(String uituCode) {
            this.uituCode = uituCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Products products = (Products) o;
            return Objects.equals(certificateDocument, products.certificateDocument) && Objects.equals(certificateDocumentDate, products.certificateDocumentDate) && Objects.equals(certificateDocumentNumber, products.certificateDocumentNumber) && Objects.equals(ownerInn, products.ownerInn) && Objects.equals(producerInn, products.producerInn) && Objects.equals(productionDate, products.productionDate) && Objects.equals(tnvedCode, products.tnvedCode) && Objects.equals(uitCode, products.uitCode) && Objects.equals(uituCode, products.uituCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(certificateDocument, certificateDocumentDate, certificateDocumentNumber, ownerInn, producerInn, productionDate, tnvedCode, uitCode, uituCode);
        }
    }
}
