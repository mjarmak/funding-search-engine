package com.jeniustech.funding_search_engine.loader;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.services.NLPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@Transactional(rollbackFor = Exception.class)
@Rollback(false)
@SpringBootTest
public class KeywordLoader {

    @Autowired
    private CallRepository callRepository;
    @Autowired
    private NLPService nlpService;

//    @Test
    void loadData() {
        List<Call> calls = callRepository.findAll();
        for (Call call : calls) {
            if (call.getKeywords() == null) {
                List<String> keywords;
                try {
                    String text = call.getAllText();
                    keywords = nlpService.getKeywords(text);
                    call.setKeywords(String.join(" ", keywords));
                    callRepository.saveAndFlush(call);
                    System.out.println(call.getId() + " : " + call.getKeywords());
                } catch (IOException e) {
                    fail("Failed to get keywords for call " + call.getId() + call.getIdentifier());
                }
            }
        }
    }
}
