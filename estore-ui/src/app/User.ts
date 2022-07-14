import { Image } from "./Image";

export interface User {
  id: string,
  userName: string;
  password: string;
  email: string;
  image: Image
  courses: string[];
  shoppingCart: string[];
  banned: boolean;
}
