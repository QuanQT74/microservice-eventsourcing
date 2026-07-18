export interface Book {
  id: string;
  name: string;
  author: string;
  isbn?: string;
  isReady: boolean;
  totalCopies?: number;
  availableCopies?: number;
  imageUrl?: string;
  description?: string;
  publisher?: string;
  publishedYear?: number;
  genre?: string;
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

export interface BorrowingResponse {
  id: string;
  bookId: string;
  employeeId: string;
  borrwingDate: string;
  returnData: string;
  status: string;
  bookName: string;
  bookAuthor: string;
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

export interface UserInfo {
  sub: string;
  preferred_username: string;
  email?: string;
  given_name?: string;
  family_name?: string;
  name?: string;
  roles?: string[];
}
