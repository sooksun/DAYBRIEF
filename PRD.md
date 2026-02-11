# PRD – DAYBRIEF
Personal Executive Secretary (Android App)

## 1. Product Vision
DAYBRIEF คือแอปเลขาส่วนตัวอัจฉริยะ สำหรับผู้บริหาร
ทำหน้าที่ช่วย “จำแทน” ในระหว่างวัน และสรุปงานให้ชัดในตอนสิ้นวัน
โดยไม่รบกวน ไม่แสดง transcript ระหว่างวัน และไม่ทำงานเองโดยไม่ได้รับคำสั่ง

## 2. Target User
- ผู้ใช้งานเพียง 1 คน (เจ้าของเครื่อง)
- ผู้บริหาร / ผู้อำนวยการ / ผู้ทำงานเชิงบริหาร
- ใช้เพื่อจัดการความคิด งานค้าง และการตัดสินใจ

## 3. Core Principles
- User-initiated only (ไม่ทำงานเอง)
- Foreground & visible (ไม่ล่องหน)
- Privacy-first
- AI assists, human decides
- Text-first, no audio retention

## 4. Core Features (MVP)
1. Daily Schedule Setup
   - ตั้งเวลาเริ่ม–สิ้นสุดรายวัน
   - กด “Start Today” เพื่อเริ่มทำงาน

2. Focus Listening Mode
   - Android Foreground Service
   - ใช้ไมโครโฟนอย่างต่อเนื่อง
   - ไม่แสดง transcript ระหว่างวัน
   - มี notification แสดงสถานะตลอดเวลา

3. Speech-to-Text Pipeline
   - แบ่งเสียงเป็นช่วง (chunk)
   - แปลงเสียงเป็นข้อความ
   - ลบไฟล์เสียงหลัง STT สำเร็จ

4. Event Segmentation
   - แยกเหตุการณ์จาก:
     - ปุ่ม Mark Event
     - ช่องว่างการพูด
     - การเปลี่ยนหัวข้อ

5. End-of-Day AI Brief
   - สรุปทั้งวัน
   - แยกเป็นเหตุการณ์
   - ระบุ:
     - Summary
     - Action items
     - Pending / Unresolved issues

6. Daily Review Screen
   - ผู้ใช้ตรวจ แก้ไข ลบ หรือเก็บบันทึก
   - Export เป็น note / task

## 5. Non-goals
- ไม่ใช้กับครูหรือองค์กร
- ไม่แชร์อัตโนมัติ
- ไม่บันทึกเสียงถาวร
- ไม่ประเมินบุคคลใด ๆ

## 6. Success Criteria
- ผู้ใช้รู้สึก “เบาขึ้น” หลังเลิกงาน
- ไม่รู้สึกถูกเฝ้าดู
- ข้อมูลทุกอย่างอยู่ในการควบคุมของผู้ใช้
