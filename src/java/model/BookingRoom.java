package model;

/**
 * Last update 23:41 02/06/2026
 *
 * @author LinhLTHE200306
 */
public class BookingRoom {

    private int bookingRoomId;
    private int bookingId;
    private int roomNumber;

    //Constructor
    public BookingRoom() {
    }

    public BookingRoom(int bookingRoomId, int bookingId, int roomNumber) {
        this.bookingRoomId = bookingRoomId;
        this.bookingId = bookingId;
        this.roomNumber = roomNumber;
    }

//Getter & Setter
    public int getBookingRoomId() {
        return bookingRoomId;
    }

    public void setBookingRoomId(int bookingRoomId) {
        this.bookingRoomId = bookingRoomId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
