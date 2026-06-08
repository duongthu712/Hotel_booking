/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import dao.HotelInfoDAO;
import model.HotelInfo;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

/**
 *
 * @author Minh Thu
 */
@WebFilter(filterName = "FooterDataFilter", urlPatterns = {"/*"})
public class FooterDataFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HotelInfoDAO hotel = new HotelInfoDAO();
        HotelInfo hotelInfo = hotel.getHotelDetails(1);
        request.setAttribute("hotelInfo", hotelInfo);
        if (hotelInfo != null && hotelInfo.getImageUrl() != null && !hotelInfo.getImageUrl().isEmpty()) {
            int lastImg = hotelInfo.getImageUrl().size() - 1;
            String lastestImg = hotelInfo.getImageUrl().get(lastImg);
            request.setAttribute("bgImage", lastestImg);
                    
        }
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
    }
}
