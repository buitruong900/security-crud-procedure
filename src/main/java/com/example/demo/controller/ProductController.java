package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.entity.ProductEntity;
import com.example.demo.service.ProductService;
import com.example.demo.service.SendMailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin("*")
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SendMailService sendMailService;

    @PostMapping("/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ProductDeleteDto productDeleteDto){
        productService.delete(productDeleteDto);
        String toEmail = "buitruong3010@gmail.com";
        String subject = "product delete successfully";
        String body = "the product with Id " +  productDeleteDto.getProId() + "has been deleted";
        sendMailService.sendMail(toEmail,subject,body);
        return ResponseEntity.ok(Collections.singletonMap("message", "delete success"));
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody ProductFindByIdDto productFindByIdDto){
        ProductEntity productEntity = productService.detail(productFindByIdDto);
        return ResponseEntity.ok(productEntity);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ProductAddDto productAddDto) {
        try {
            productService.add(productAddDto);
            return ResponseEntity.ok(Collections.singletonMap("message", "add success"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error add",e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ProductUpdateDto productUpdateDto){
        productService.update(productUpdateDto);
        return ResponseEntity.ok(Collections.singletonMap("message", "update success"));
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchProcedure(@RequestBody ProductSeachDto productSeachDto){
        Pageable pageable = PageRequest.of(productSeachDto.getPage(),productSeachDto.getSize());
        return ResponseEntity.ok(productService.findByPagingCriteria(productSeachDto,pageable));
    }
    @PostMapping("/export-excel")
    public ResponseEntity<?> exportExcel(@RequestBody ProductSeachDto productSeachDto, HttpServletResponse httpServletResponse){
        try {
            List<ProductEntity> productEntities = productService.exportExcelByFindJpa(productSeachDto);
            httpServletResponse.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=products.xlsx";
            httpServletResponse.setHeader(headerKey, headerValue);
            // tao file excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Products");
            CellStyle cellStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            // tao ten cot
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ProName");
            headerRow.createCell(1).setCellValue("Producer");
            headerRow.createCell(2).setCellValue("YearMaking");
            headerRow.createCell(3).setCellValue("Quality");
            headerRow.createCell(4).setCellValue("Price");
            headerRow.createCell(5).setCellValue("ExpDate");

            int rowIdn = 1;
            for (ProductEntity productEntity : productEntities){
                Row row = sheet.createRow(rowIdn++);
                row.createCell(0).setCellValue(productEntity.getProName());
                row.createCell(1).setCellValue(productEntity.getProducer());
                row.createCell(2).setCellValue(productEntity.getYearMaking());
                row.createCell(3).setCellValue(productEntity.getQuality());
                row.createCell(4).setCellValue(productEntity.getPrice());

                Cell expDateCell = row.createCell(5);
                expDateCell.setCellValue(productEntity.getExpDate());
                expDateCell.setCellStyle(cellStyle);
            }
            OutputStream outputStream = httpServletResponse.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return ResponseEntity.ok("file excel đã được xuất thành công!");
        }catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("co loi xay ra !");
        }
    }
    @PostMapping("/import-excel")
    public ResponseEntity<?> importProductFormExcel(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            return ResponseEntity.badRequest().body("File must be an Excel file.");
        }
        try {
            List<ProductDto> productDtos = productService.importExcel(file);
            return ResponseEntity.ok().body(productDtos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Import errors: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file.");
        }
    }
    @PostMapping("/import-excelSkipDuplicate")
    public ResponseEntity<?> importExcelSkipDuplicate(@RequestParam("file")MultipartFile file) throws IOException{
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("no file upload");
        }
        String fileName =  file.getOriginalFilename();
        if(!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")){
            return ResponseEntity.badRequest().body("file must be an excel file");
        }
        try {
            List<ProductDto> productDtos = productService.importExcelCheckDuplicate(file);
            return ResponseEntity.ok().body(productDtos);
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error processing the file");
        }
    }

}
