package ma.enset.exercice2.controller;

import ma.enset.exercice2.model.SalesSummary;
import ma.enset.exercice2.repository.SalesSummaryRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job salesJob;

    @Autowired
    private SalesSummaryRepository summaryRepository;

    @PostMapping("/job/start")
    public String startJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        
        jobLauncher.run(salesJob, params);
        return "Job started...";
    }

    @GetMapping("/summaries")
    public List<SalesSummary> getSummaries() {
        return summaryRepository.findAll();
    }
} 