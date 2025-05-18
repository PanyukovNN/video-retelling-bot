package ru.panyukovnn.videoretellingbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.videoretellingbot.dto.ConsumeContentRequest;
import ru.panyukovnn.videoretellingbot.dto.common.CommonRequest;
import ru.panyukovnn.videoretellingbot.dto.common.CommonResponse;
import ru.panyukovnn.videoretellingbot.serivce.ContentConsumerHandler;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentConsumerController {

    private final ContentConsumerHandler contentConsumerHandler;

    @PostMapping("/consume")
    public CommonResponse<Void> consume(@RequestBody @Valid CommonRequest<ConsumeContentRequest> request) {
        contentConsumerHandler.handleConsumeContent(request.getBody());

        return CommonResponse.<Void>builder().build();
    }
}
