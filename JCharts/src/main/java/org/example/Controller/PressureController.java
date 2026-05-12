package org.example.Controller;



import org.example.Data.MetricsResponse;
import org.example.Data.TestRequest;
import org.example.Service.PressureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class PressureController {

    private final PressureService pressureService;

    public PressureController(PressureService pressureService) {
        this.pressureService = pressureService;
    }

    @PostMapping("/start")
    public String start(@RequestBody TestRequest request) {
        pressureService.start(request);
        return "压测启动成功";
    }

    @PostMapping("/stop")
    public String stop() {
        pressureService.stop();
        return "压测已停止";
    }

    @GetMapping("/metrics")
    public MetricsResponse metrics() {
        return pressureService.getMetrics();
    }
}