package kz.qasqir.qasqirinventory.api.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.qasqir.qasqirinventory.api.service.Bitrix24Service;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
@Component
public class Bitrix24Interceptor implements HandlerInterceptor {

    private final Bitrix24Service bitrix24Service;

    public Bitrix24Interceptor(Bitrix24Service bitrix24Service) {
        this.bitrix24Service = bitrix24Service;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
}