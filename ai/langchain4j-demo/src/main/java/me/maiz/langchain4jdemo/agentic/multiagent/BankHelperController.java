package me.maiz.langchain4jdemo.agentic.multiagent;

import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.agentic.multiagent.agents.BankSupervisorAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BankHelperController {

    @Autowired
//    private BankSupervisorAgent supervisorAgent;

    private SupervisorAgent  supervisorAgent;

    @RequestMapping("/manager")
    public String manager(@RequestParam("question") String question) {
        log.info("manager: question={}", question);
        return supervisorAgent.invoke(question);
    }

}
