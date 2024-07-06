package com.jzel.citycountryriver_llama.rest;

import static java.util.Objects.requireNonNull;

import com.jzel.citycountryriver_llama.chat.AnswerEvaluation;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

  private final ChatClient chatClient;

  // TODO implement requiring a specific character at the beginning of the answer

  // example category: "a joke that the ai thinks is funny"
  // TODO add more categories
  @PostMapping("/answer/{answer}")
  public ResponseEntity<Boolean> answer(@PathVariable String answer) {
    final var outputParser = new BeanOutputConverter<>(AnswerEvaluation.class);
    try {
      return ResponseEntity.ok(
        requireNonNull(
            outputParser.convert(
                chatClient.prompt(
                        new PromptTemplate(
                            "Is the answer {answer} a sentence with a funny joke? {format}",
                            Map.of("answer", answer, "format", outputParser.getFormat())
                        ).create()
                    )
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getContent()
            )
        ).answerIsCorrect()
    );
    } catch (Exception e) {
      return answer(answer);
    }
  }
}
