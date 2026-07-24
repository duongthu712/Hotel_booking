/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.HotelImage;
import model.HotelInfo;
import model.HotelNews;
import model.HotelService;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author Minh Thu
 */
public class HotelInfoDAO extends DBContext {
    
    // Author: ThuDNM-HE204370

    // Author: ThuDNM-HE204370
    // Lấy các service còn hoạt động để hiện trên homepage 
    public List<HotelService> getActiveHotelServices() {
        List<HotelService> list = new ArrayList<>();
        String sql = "SELECT hotel_service_id, service_name, hotel_id, [description], unit_price, image_url, is_active "
                + "FROM HotelServices WHERE is_active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelService service = new HotelService(
                        rs.getInt("hotel_service_id"),
                        rs.getNString("service_name"),
                        rs.getInt("hotel_id"),
                        rs.getNString("description"),
                        rs.getBigDecimal("unit_price"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_active")
                );
                list.add(service);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Author: ThuDNM-HE204370
    // Lấy thông tin khách sạn để hiện trên hompage
    public HotelInfo getHotelDetails(int hotelId) {
        HotelInfo info = null;
        try {
            // Lấy thông tin cho footer
            String sqlHotel = "SELECT hotel_id, hotel_name, [description], "
                    + "CAST(checkin_time AS TIME(0)) AS checkin, "
                    + "CAST(checkout_time AS TIME(0)) AS checkout, "
                    + "[address], address_url, phone, email FROM HotelInfo WHERE hotel_id = ?";
            PreparedStatement stm = connection.prepareStatement(sqlHotel);
            stm.setInt(1, hotelId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                info = new HotelInfo(
                        rs.getInt("hotel_id"),
                        rs.getNString("hotel_name"),
                        rs.getNString("description"),
                        rs.getTime("checkin") != null ? rs.getTime("checkin").toLocalTime() : LocalTime.of(14, 0),
                        rs.getTime("checkout") != null ? rs.getTime("checkout").toLocalTime() : LocalTime.of(12, 0),
                        rs.getNString("address"),
                        rs.getString("address_url"),
                        rs.getString("phone"),
                        rs.getString("email")
                );

                // Lấy ảnh background
                String sqlImages = "SELECT TOP 1 image_id, image_url, hotel_id, caption, image_type "
                        + "FROM HotelImages WHERE hotel_id = ? AND image_type = N'Ảnh nền' "
                        + "ORDER BY image_id DESC";
                PreparedStatement stmImg = connection.prepareStatement(sqlImages);
                stmImg.setInt(1, hotelId);
                ResultSet rsImages = stmImg.executeQuery();
                while (rsImages.next()) {
                    HotelImage img = new HotelImage(
                            rsImages.getInt("image_id"),
                            rsImages.getString("image_url"),
                            rsImages.getInt("hotel_id"),
                            rsImages.getNString("caption"),
                            rsImages.getNString("image_type")
                    );
                    info.addImage(img);
                }
                rsImages.close();
                stmImg.close();
            }
            rs.close();
            stm.close();
        } catch (Exception e) {
            System.out.println("getHotelDetails: " + e.getMessage());
        }
        return info;
    }

    // Author: ThuDNM-HE204370
    // Lấy 3 bài báo mới nhất
    public List<HotelNews> getTop3LatestNews() {
        List<HotelNews> list = new ArrayList<>();
        String sql = "SELECT TOP 3 news_id, hotel_id, title, content, image_url, is_active, created_at, created_by "
                + "FROM HotelNews WHERE is_active = 1 ORDER BY created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelNews news = new HotelNews(
                        rs.getInt("news_id"),
                        rs.getInt("hotel_id"),
                        rs.getNString("title"),
                        rs.getNString("content"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("created_by")
                );
                list.add(news);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Author: ThuDNM-HE204370
    // Lấy 6 ảnh để hiển thị ở mục Không gian lưu trú
    public List<HotelImage> get6SmallImages(int hotelId) {
        List<HotelImage> list = new ArrayList<>();
        String sql = "SELECT TOP 6 image_id, image_url, hotel_id, caption, image_type "
                + "FROM HotelImages WHERE hotel_id = ? AND image_type = N'Ảnh nhỏ' "
                + "ORDER BY image_id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, hotelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HotelImage img = new HotelImage(
                        rs.getInt("image_id"),
                        rs.getString("image_url"),
                        rs.getInt("hotel_id"),
                        rs.getNString("caption"),
                        rs.getNString("image_type")
                );
                list.add(img);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //LinhLTHE200306
    public HotelInfo getHotelInfoById(int hotelId) throws Exception {
        String strSQL = """
                    select * 
                    from HotelInfo 
                    where hotel_id = ?
                    """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, hotelId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    HotelInfo hotel = new HotelInfo();
                    hotel.setHotelId(rs.getInt("hotel_id"));
                    hotel.setHotelName(rs.getString("hotel_name"));
                    hotel.setDescription(rs.getString("description"));

                    java.sql.Time checkinSql = rs.getTime("checkin_time");
                    java.sql.Time checkoutSql = rs.getTime("checkout_time");
                    hotel.setCheckinTime(checkinSql != null
                            ? checkinSql.toLocalTime()
                            : java.time.LocalTime.of(14, 0));
                    hotel.setCheckoutTime(checkoutSql != null
                            ? checkoutSql.toLocalTime()
                            : java.time.LocalTime.of(12, 0));

                    hotel.setAddress(rs.getString("address"));
                    hotel.setAddressUrl(rs.getString("address_url"));
                    hotel.setPhone(rs.getString("phone"));
                    hotel.setEmail(rs.getString("email"));

                    return hotel;
                } else {
                    throw new Exception("Không tìm thấy thông tin khách sạn.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin khách sạn.");
        }
    }

    public HotelInfo updateHotelInfo(HotelInfo hotelInfo) throws Exception {
        HotelInfo found = getHotelInfoById(hotelInfo.getHotelId());
        if (found == null) {
            throw new Exception("Khách sạn không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update HotelInfo 
                        set hotel_name = ?, 
                        [description] = ?, 
                        checkin_time = ?, 
                        checkout_time = ?, 
                        [address] = ?, 
                        address_url = ?, 
                        phone = ?, 
                        email = ?  
                        where hotel_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, hotelInfo.getHotelName());
            stm.setString(2, hotelInfo.getDescription());
            stm.setObject(3, hotelInfo.getCheckinTime());
            stm.setObject(4, hotelInfo.getCheckoutTime());
            stm.setString(5, hotelInfo.getAddress());
            stm.setString(6, hotelInfo.getAddressUrl());
            stm.setString(7, hotelInfo.getPhone());
            stm.setString(8, hotelInfo.getEmail());
            stm.setInt(9, hotelInfo.getHotelId());

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return hotelInfo;
            } else {
                throw new Exception("Cập nhật thông tin khách sạn thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật thông tin khách sạn.");
        }
    }

    public List<HotelImage> getImagesByHotelId(int hotelId) throws Exception {
        List<HotelImage> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from HotelImages 
                        where hotel_id = ?  
                        order by image_id
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, hotelId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    HotelImage img = new HotelImage();
                    img.setImageId(rs.getInt("image_id"));
                    img.setImageUrl(rs.getString("image_url"));
                    img.setHotelId(rs.getInt("hotel_id"));
                    img.setCaption(rs.getString("caption"));
                    img.setImageType(rs.getString("image_type"));
                    list.add(img);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách ảnh.");
        }
        return list;
    }

    public HotelImage getImageById(int imageId) throws Exception {
        String strSQL = """
                        select * 
                        from HotelImages 
                        where image_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, imageId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    HotelImage img = new HotelImage();
                    img.setImageId(rs.getInt("image_id"));
                    img.setImageUrl(rs.getString("image_url"));
                    img.setHotelId(rs.getInt("hotel_id"));
                    img.setCaption(rs.getString("caption"));
                    img.setImageType(rs.getString("image_type"));
                    return img;
                } else {
                    throw new Exception("Ảnh không tồn tại trong hệ thống.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin ảnh.");
        }
    }

    public HotelImage getBannerByHotelId(int hotelId) throws Exception {
        String strSQL = """
                        select * 
                        from HotelImages 
                        where hotel_id = ? 
                        and image_type = N'Ảnh nền'
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, hotelId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    HotelImage img = new HotelImage();
                    img.setImageId(rs.getInt("image_id"));
                    img.setImageUrl(rs.getString("image_url"));
                    img.setHotelId(rs.getInt("hotel_id"));
                    img.setCaption(rs.getString("caption"));
                    img.setImageType(rs.getString("image_type"));
                    return img;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy ảnh banner.");
        }
        return null;
    }

    public List<HotelImage> getSmallImagesByHotelId(int hotelId) throws Exception {
        List<HotelImage> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from HotelImages 
                        where hotel_id = ? 
                        and image_type = N'Ảnh nhỏ' 
                        order by image_id
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, hotelId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    HotelImage img = new HotelImage();
                    img.setImageId(rs.getInt("image_id"));
                    img.setImageUrl(rs.getString("image_url"));
                    img.setHotelId(rs.getInt("hotel_id"));
                    img.setCaption(rs.getString("caption"));
                    img.setImageType(rs.getString("image_type"));
                    list.add(img);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách ảnh nhỏ.");
        }
        return list;
    }

    public HotelImage updateImage(int imageId, String newImageUrl, String caption) throws Exception {
        HotelImage found = getImageById(imageId);
        if (found == null) {
            throw new Exception("Ảnh không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update HotelImages 
                        set image_url = ?, 
                            caption = ? 
                        where image_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, newImageUrl);
            stm.setString(2, caption);
            stm.setInt(3, imageId);

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                found.setImageUrl(newImageUrl);
                found.setCaption(caption);
                return found;
            } else {
                throw new Exception("Cập nhật ảnh thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật ảnh.");
        }
    }

    public List<HotelNews> getAllNewsByHotelId(int hotelId) throws Exception {
        List<HotelNews> list = new ArrayList<>();
        String strSQL = """
                        select * 
                        from HotelNews 
                        where hotel_id = ? 
                        order by is_active desc, created_at desc
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, hotelId);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    HotelNews news = new HotelNews();
                    news.setNewsId(rs.getInt("news_id"));
                    news.setHotelId(rs.getInt("hotel_id"));
                    news.setTitle(rs.getString("title"));
                    news.setContent(rs.getString("content"));
                    news.setImageUrl(rs.getString("image_url"));
                    news.setActive(rs.getBoolean("is_active"));
                    news.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    news.setCreatedBy(rs.getInt("created_by"));
                    list.add(news);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách tin tức.");
        }
        return list;
    }

    public HotelNews getNewsById(int newsId) throws Exception {
        String strSQL = """
                        select * 
                        from HotelNews 
                        where news_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, newsId);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    HotelNews news = new HotelNews();
                    news.setNewsId(rs.getInt("news_id"));
                    news.setHotelId(rs.getInt("hotel_id"));
                    news.setTitle(rs.getString("title"));
                    news.setContent(rs.getString("content"));
                    news.setImageUrl(rs.getString("image_url"));
                    news.setActive(rs.getBoolean("is_active"));
                    news.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    news.setCreatedBy(rs.getInt("created_by"));
                    return news;
                } else {
                    throw new Exception("Bài viết không tồn tại trong hệ thống.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy thông tin bài viết.");
        }
    }

    public HotelNews createNews(HotelNews news) throws Exception {
        String strSQL = """
                        insert into HotelNews (hotel_id, title, content, image_url, is_active, created_by) 
                        values (?, ?, ?, ?, ?, ?)
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, news.getHotelId());
            stm.setString(2, news.getTitle());
            stm.setString(3, news.getContent());
            stm.setString(4, news.getImageUrl());
            stm.setBoolean(5, news.isActive());
            stm.setInt(6, news.getCreatedBy());

            if (stm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        news.setNewsId(generatedKeys.getInt(1));
                    }
                }
                return news;
            } else {
                throw new Exception("Thêm bài viết thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tạo bài viết mới.");
        }
    }

    public HotelNews updateNews(HotelNews news) throws Exception {
        HotelNews found = getNewsById(news.getNewsId());
        if (found == null) {
            throw new Exception("Bài viết không tồn tại, không thể cập nhật.");
        }

        String strSQL = """
                        update HotelNews 
                        set title = ?, 
                        content = ?, 
                        image_url = ?, 
                        is_active = ? 
                        where news_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setString(1, news.getTitle());
            stm.setString(2, news.getContent());
            stm.setString(3, news.getImageUrl());
            stm.setBoolean(4, news.isActive());
            stm.setInt(5, news.getNewsId());

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return news;
            } else {
                throw new Exception("Cập nhật bài viết thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể cập nhật bài viết.");
        }
    }

    public HotelNews deleteNews(int newsId) throws Exception {
        HotelNews found = getNewsById(newsId);
        if (found == null) {
            throw new Exception("Bài viết không tồn tại, không thể xóa.");
        }

        if (found.isActive()) {
            throw new Exception("Bài viết đang hoạt động, vui lòng ngừng hoạt động trước khi xóa.");
        }

        String strSQL = """
                        delete 
                        from HotelNews 
                        where news_id = ?
                        """;

        try (PreparedStatement stm = connection.prepareStatement(strSQL)) {
            stm.setInt(1, newsId);

            int rowCount = stm.executeUpdate();
            if (rowCount > 0) {
                return found;
            } else {
                throw new Exception("Xóa bài viết thất bại.");
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể xóa bài viết.");
        }
    }

    public List<HotelNews> searchNewsByTitle(int hotelId, String keyword, String status) throws Exception {
        List<HotelNews> list = new ArrayList<>();
        StringBuilder strSQL = new StringBuilder("""
                        select * 
                        from HotelNews 
                        where hotel_id = ? 
                        """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            strSQL.append("and title like ? ");
        }

        if ("active".equals(status)) {
            strSQL.append("and is_active = 1 ");
        } else if ("inactive".equals(status)) {
            strSQL.append("and is_active = 0 ");
        }

        strSQL.append("order by is_active desc, created_at desc");

        try (PreparedStatement stm = connection.prepareStatement(strSQL.toString())) {
            int paramIndex = 1;
            stm.setInt(paramIndex++, hotelId);

            if (keyword != null && !keyword.trim().isEmpty()) {
                stm.setString(paramIndex++, "%" + keyword.trim() + "%");
            }

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    HotelNews news = new HotelNews();
                    news.setNewsId(rs.getInt("news_id"));
                    news.setHotelId(rs.getInt("hotel_id"));
                    news.setTitle(rs.getString("title"));
                    news.setContent(rs.getString("content"));
                    news.setImageUrl(rs.getString("image_url"));
                    news.setActive(rs.getBoolean("is_active"));
                    news.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    news.setCreatedBy(rs.getInt("created_by"));
                    list.add(news);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể tìm kiếm bài viết.");
        }
        return list;
    }

    public String getHotelName() {
        String sql = "SELECT TOP 1 hotel_name FROM HotelInfo";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("hotel_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "La Mer";
    }
}
