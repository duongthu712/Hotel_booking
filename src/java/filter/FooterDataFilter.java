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
import java.util.List;
import model.HotelImage;

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
        
        if (hotelInfo != null && hotelInfo.getImages() != null && !hotelInfo.getImages().isEmpty()) {
            List<HotelImage> imgs = hotelInfo.getImages();
            HotelImage latestImgObj = imgs.get(0);
            request.setAttribute("bgImage", latestImgObj.getImageUrl());
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
