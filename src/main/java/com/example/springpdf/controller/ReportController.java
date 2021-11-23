package com.example.springpdf.controller;

import com.example.springpdf.model.Course;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class ReportController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public byte[] generateSimplePDF() {
        ByteArrayOutputStream pfdStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf("<h1>Test</h1>", pfdStream);
        return pfdStream.toByteArray();
    }


    @GetMapping(value = "report", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public byte[] generateHtmlToPDF() throws IOException {
        ByteArrayOutputStream pfdStream = new ByteArrayOutputStream();
        Resource htmlStream = resourceLoader.getResource("classpath:report.html");
        HtmlConverter.convertToPdf(htmlStream.getInputStream(), pfdStream);
        return pfdStream.toByteArray();
    }

    @GetMapping(value = "courses", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public byte[] generatePDFByTemplateThymeleaf() {
        //Mock da lista de cursos
        List<Course> listCourses = new ArrayList<>();
        listCourses.add(new Course("Spring Framework", "Greco", 48));
        listCourses.add(new Course("Spring Data JPA", "Greco", 48));
        listCourses.add(new Course("Spring Security", "Greco", 48));

        //Seta no contexto os valores na variavel courses declarada no template
        Context context = new Context();
        context.setVariable("courses", listCourses);
        //Processa o template HTML e o contexto
        String html = templateEngine.process("courses.html", context);
        //Converte uma string HTML para PDF
        ByteArrayOutputStream pfdStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, pfdStream);
        //Retorna um array de byte
        return pfdStream.toByteArray();
    }


    @GetMapping(value = "courses/download", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<?> downloadPDFByTemplateThymeleaf() {
        List<Course> listCourses = new ArrayList<>();
        listCourses.add(new Course("Spring Framework", "Greco", 48));
        listCourses.add(new Course("Spring Data JPA", "Greco", 48));
        listCourses.add(new Course("Spring Security", "Greco", 48));

        Context context = new Context();
        context.setVariable("courses", listCourses);

        String html = templateEngine.process("courses.html", context);

        ByteArrayOutputStream pfdStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, pfdStream);
        //Retorna um reponse entity passando um parametro de attachment para informar
        //o client que deve efetuar o download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=courses.pdf")
                .body(pfdStream.toByteArray());
    }
}
