package ir.maktabsharif.home_service.controller.expert;

import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expert")
@RequiredArgsConstructor
@Tag(name = "Expert controller",description = "controller class for expert")
public class ExpertController {
    private final ExpertService expertService;


}
