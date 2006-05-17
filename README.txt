*** Tokyo Project ***

XML has now been widely adopted as a standard for storage, exchange and representation of structured information.

XML is a good compromise between (human) readability and efficient (machine) processing, but it is still not good enough for many uses that do not allow any concession.
Two common examples are programming and databases. For the former, full text is used because no compromise on source code readability is acceptable, for the latter data is stored as binary because no compromise on processing efficiency is acceptable.

The goal of the Tokyo Project is to design a process, an architecture, and tools to provide an XML view of non-XML data. This way, applications can view and create non-XML documents as easily as XML ones.

A simple business case is the transformation of non-XML documents to XML and back again (anything-to-xml-to-anything). Therefore, this project may also prove useful to manage binary documents created by commercial applications, as long as their file format is available.

The Tokyo Project is hosted on Sourceforge:
http://sourceforge.net/projects/tokyo/

Copyright (C) 2005 Eric Bréchemier
http://eric.brechemier.name

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA