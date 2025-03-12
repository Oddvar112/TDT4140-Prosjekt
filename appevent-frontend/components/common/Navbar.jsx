"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { PlusCircle, Calendar, User, Search } from "lucide-react";
import { usePathname } from 'next/navigation';

const Navbar = () => {
  const pathname = usePathname();
  
  if (pathname === '/login') {
    return null;
  }

  return (
    <nav className="w-full sticky top-0 border-b-2 border-zinc-300 bg-zinc-200 z-10">
      <div className="max-w-screen-xl mx-auto flex h-16 items-center px-4">
        <Link href="/upcoming-events" className="flex items-center space-x-2">
          <Calendar className="h-6 w-6" />
          <span className="font-bold text-xl">AppEvent</span>
        </Link>

        <div className="flex-1" />

        <div className="flex items-center space-x-4">
          <Link href="/search">
            <Button
              variant="ghost"
              size="sm"
              className="gap-2 font-bold text-base"
              >
                <Search className="h-4 w-4" />
              </Button>
          </Link>

          <Link href="/create-event">
            <Button
              variant="ghost"
              size="sm"
              className="gap-2 font-bold text-base"
            >
              <PlusCircle className="h-4 w-4" />
              Opprett Arrangement
            </Button>
          </Link>

          <Link href="/profile">
            <Button variant="ghost" size="icon">
              <User className="h-4 w-4" />
            </Button>
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;