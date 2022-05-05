import { Image } from './Image';
import { lesson } from './lesson';

export interface Course {
  id: number;
  image: Image;
  title: string;
  price: number;
  description: string;
  studentsEnrolled: number;
  tags: string[];
  content: lesson[];
}
