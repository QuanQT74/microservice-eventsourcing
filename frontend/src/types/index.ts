export interface Book {
  id: string;
  name: string;
  author: string;
  isReady: boolean;
}

export interface Employee {
  id: string;
  firstName: string;
  lastName: string;
  Kin: string;
  isDisciplined: boolean;
}

export interface BorrowingRequest {
  bookId: string;
  employeeId: string;
}

export interface LocalBorrowing {
  id: string;
  bookId: string;
  bookName: string;
  bookAuthor: string;
  employeeId: string;
  borrowedDate: string;
  status: "ACTIVE" | "RETURNED";
}

export interface ApiError {
  message: string;
  status?: number;
}
