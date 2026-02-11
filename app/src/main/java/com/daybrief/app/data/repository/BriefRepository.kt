package com.daybrief.app.data.repository

import com.daybrief.app.data.db.DailyBriefDao
import com.daybrief.app.data.db.DailyBriefEntity
import com.daybrief.app.viewmodel.ActionItem
import com.daybrief.app.viewmodel.DayEvent
import com.daybrief.app.viewmodel.PendingIssue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repository for daily briefs — abstracts data access.
 * Reads/writes to Room (SQLite) database.
 */
class BriefRepository(
    private val dao: DailyBriefDao
) {

    /**
     * Gets brief for a specific date (suspend for one-shot read).
     */
    suspend fun getBrief(date: LocalDate): DailyBriefEntity? =
        dao.getByDate(date.toEpochDay())

    /**
     * Observes brief for a date. Emits null if not found.
     */
    fun observeBrief(date: LocalDate): Flow<DailyBriefEntity?> =
        dao.observeByDate(date.toEpochDay())

    /**
     * Gets all briefs ordered by date (newest first).
     */
    fun getAllBriefs(): Flow<List<DailyBriefEntity>> =
        dao.getAllByDateDesc()

    /**
     * Gets list of dates that have briefs (for report picker).
     */
    fun getAllDates(): Flow<List<LocalDate>> =
        dao.getAllDatesDesc().map { epochDays ->
            epochDays.map { LocalDate.ofEpochDay(it) }
        }

    /**
     * Saves or updates a brief for the given date.
     */
    suspend fun saveBrief(
        date: LocalDate,
        listeningDuration: String,
        overviewSummary: String,
        actionItems: List<ActionItem>,
        pendingIssues: List<PendingIssue>,
        events: List<DayEvent>
    ) {
        val entity = DailyBriefEntity(
            dateEpochDay = date.toEpochDay(),
            listeningDuration = listeningDuration,
            overviewSummary = overviewSummary,
            actionItems = actionItems,
            pendingIssues = pendingIssues,
            events = events,
            createdAt = System.currentTimeMillis()
        )
        dao.insert(entity)
    }

    /**
     * Deletes brief for a specific date.
     */
    suspend fun deleteByDate(date: LocalDate) {
        dao.deleteByDate(date.toEpochDay())
    }

    /**
     * Deletes all briefs.
     */
    suspend fun deleteAll() {
        dao.deleteAll()
    }

    /**
     * Seeds demo data for the last 7 days if DB is empty.
     * Called on first launch so user can try "ดูย้อนหลัง".
     */
    suspend fun seedDemoIfEmpty() {
        val existing = dao.getByDate(LocalDate.now().toEpochDay())
        if (existing != null) return // Already has data
        val today = LocalDate.now()
        for (offset in 0..6) {
            val date = today.minusDays(offset.toLong())
            val content = buildDemoForOffset(offset)
            dao.insert(
                DailyBriefEntity(
                    dateEpochDay = date.toEpochDay(),
                    listeningDuration = content.listeningDuration,
                    overviewSummary = content.overviewSummary,
                    actionItems = content.actionItems,
                    pendingIssues = content.pendingIssues,
                    events = content.events,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun buildDemoForOffset(offset: Int): DemoContent = when (offset) {
        0 -> DemoContent(
            "08:32",
            "วันนี้มีการประชุมหลัก 3 ครั้ง เน้นเรื่องความคืบหน้าโปรเจกต์และงบประมาณ ลูกค้า ABC Corp ตอบรับ demo ดี มี action items สำคัญ 3 เรื่อง และมีเรื่องค้างคาที่ต้องติดตาม 2 เรื่อง",
            listOf(
                ActionItem("a1", "ส่งรายงานสรุปไตรมาส 1 ให้ทีมบัญชีภายในวันศุกร์"),
                ActionItem("a2", "นัดประชุม follow-up กับทีม Marketing เรื่องแคมเปญใหม่"),
                ActionItem("a3", "ตรวจสอบ proposal ที่ทีมพัฒนาส่งมา แล้วส่ง feedback กลับ")
            ),
            listOf(
                PendingIssue("p1", "งบประมาณโปรเจกต์ B ยังไม่ได้รับอนุมัติ", "รอ CFO ลงนาม"),
                PendingIssue("p2", "ยังไม่ได้ตอบอีเมลจากลูกค้า XYZ เรื่องเลื่อนกำหนดส่งงาน", "")
            ),
            listOf(
                DayEvent("e1", "09:15", "ประชุมทีม Morning Standup", "ทีมรายงานความคืบหน้า sprint ปัจจุบัน มี 2 task ติดปัญหา API"),
                DayEvent("e2", "10:30", "โทรคุยกับลูกค้า ABC Corp", "ลูกค้าพอใจกับ demo ต้องการเพิ่มฟีเจอร์ export PDF"),
                DayEvent("e3", "13:00", "ประชุมงบประมาณไตรมาส 2", "ปรับลดงบ Marketing 10% เพิ่มงบ R&D สำหรับ AI project"),
                DayEvent("e4", "15:30", "Review แผนแคมเปญ", "แคมเปญ Social Media เปิดตัวเดือนหน้า ต้องเตรียม content")
            )
        )
        1 -> DemoContent(
            "07:45",
            "เมื่อวานเน้นประชุมทีมและ one-on-one กับ staff 2 คน มี follow-up เรื่อง KPI Q1 และการรับสมัครตำแหน่งใหม่ เรื่องค้างคา 1 เรื่อง.",
            listOf(
                ActionItem("b1", "ส่งสรุป KPI Q1 ให้ HR ภายในวันพุธ"),
                ActionItem("b2", "โทรนัดสัมภาษณ์ candidate ตำแหน่ง Developer 2 คน")
            ),
            listOf(PendingIssue("q1", "รอ Legal อนุมัติสัญญาโครงการใหม่", "")),
            listOf(
                DayEvent("f1", "09:00", "ประชุมทีมสัปดาห์", "Review KPI และแผน Q2"),
                DayEvent("f2", "11:00", "One-on-one กับทีม Lead", "พูดคุยเรื่อง workload และ training"),
                DayEvent("f3", "14:30", "ประชุม HR เรื่องการรับสมัคร", "เปิดรับ 2 ตำแหน่ง เริ่มสัปดาห์หน้า")
            )
        )
        2 -> DemoContent(
            "06:20",
            "วันศุกร์ที่ผ่านมาเน้นปิดงานสัปดาห์ ส่งรายงานและสรุป meeting กับลูกค้าใหญ่ 1 ราย ไม่มีเรื่องค้างคา.",
            listOf(ActionItem("c1", "จัดส่งรายงานสัปดาห์ให้ลูกค้า XYZ (ทำแล้ว)")),
            emptyList(),
            listOf(
                DayEvent("g1", "10:00", "ประชุมปิดสัปดาห์", "สรุปความคืบหน้าและแผนสัปดาห์หน้า"),
                DayEvent("g2", "14:00", "โทรสรุปงานกับลูกค้า ABC Corp", "ลูกค้าพอใจความคืบหน้า โครงการ on track")
            )
        )
        3 -> DemoContent(
            "08:00",
            "วันพฤหัสบดีมีการประชุมหลายรอบ เรื่องงบประมาณและ roadmap ผลิตภัณฑ์ มี action เกี่ยวกับการเสนอขออนุมัติเพิ่ม.",
            listOf(
                ActionItem("d1", "จัดทำเอกสารเสนอขออนุมัติงบเพิ่มสำหรับโปรเจกต์ AI"),
                ActionItem("d2", "อัปเดต roadmap ผลิตภัณฑ์ส่งให้ทีมบริหาร")
            ),
            listOf(PendingIssue("r1", "รอผลการประชุมบอร์ดเรื่องงบ Q3", "")),
            listOf(
                DayEvent("h1", "09:30", "ประชุมงบประมาณ Q3", "เสนอแผนจัดสรรงบ"),
                DayEvent("h2", "13:00", "ประชุม Product Roadmap", "ปรับ timeline ฟีเจอร์ใหม่"),
                DayEvent("h3", "15:00", "Sync กับทีม Design", "Review wireframe หน้าใหม่")
            )
        )
        4 -> DemoContent(
            "05:50",
            "วันพุธเน้นงานเอกสารและ review ข้อเสนอจาก vendor 2 ราย มีการโทรติดตามลูกค้า 1 ราย.",
            listOf(
                ActionItem("e1", "เปรียบเทียบข้อเสนอ vendor A และ B ส่งสรุปให้ CFO"),
                ActionItem("e2", "ส่ง meeting notes ให้ทีมที่เกี่ยวข้อง")
            ),
            emptyList(),
            listOf(
                DayEvent("i1", "10:00", "Review ข้อเสนอจาก vendor", "เปรียบเทียบราคาและ scope"),
                DayEvent("i2", "14:00", "โทรติดตามลูกค้า DEF", "ยืนยันกำหนดส่งงานและความต้องการเพิ่ม")
            )
        )
        5 -> DemoContent(
            "07:10",
            "วันอังคารมีการ training ทีมเรื่องระบบใหม่และประชุม kickoff โปรเจกต์เล็ก 1 โปรเจกต์.",
            listOf(
                ActionItem("f1", "จัดเตรียม material training ครั้งถัดไป"),
                ActionItem("f2", "ส่ง timeline โปรเจกต์ใหม่ให้ stakeholder")
            ),
            listOf(PendingIssue("s1", "รอ IT เปิด access ระบบใหม่ให้ทีม", "")),
            listOf(
                DayEvent("j1", "09:00", "Training ทีมเรื่องระบบ CRM ใหม่", "ทีมใช้ได้ครบ ต้อง follow-up อีก 1 รอบ"),
                DayEvent("j2", "11:30", "Kickoff โปรเจกต์ Website Redesign", "กำหนดส่งต้นเดือนหน้า"),
                DayEvent("j3", "15:00", "ประชุมสั้นกับทีม Support", "ประเด็นเรื่อง response time")
            )
        )
        6 -> DemoContent(
            "08:15",
            "วันจันทร์เริ่มสัปดาห์ด้วยการประชุม All-hands และ one-on-one กับทีม มีประเด็นเรื่องการจ้างงานและ OKR ไตรมาส.",
            listOf(
                ActionItem("g1", "อัปเดต OKR Q2 ตามที่ประชุมตกลง"),
                ActionItem("g2", "ส่ง job description ตำแหน่งใหม่ให้ HR")
            ),
            listOf(PendingIssue("t1", "รอ approval เรื่อง headcount เพิ่ม", "จาก CEO")),
            listOf(
                DayEvent("k1", "09:00", "All-hands สัปดาห์", "ประกาศ OKR และความคืบหน้าทั้งบริษัท"),
                DayEvent("k2", "10:30", "One-on-one กับ 3 ทีม", "เรื่องเป้าหมายและ blockers"),
                DayEvent("k3", "14:00", "ประชุม HR เรื่อง Headcount", "ขออนุมัติเพิ่ม 2 ตำแหน่ง")
            )
        )
        else -> buildDemoForOffset(0)
    }
}

data class DemoContent(
    val listeningDuration: String,
    val overviewSummary: String,
    val actionItems: List<ActionItem>,
    val pendingIssues: List<PendingIssue>,
    val events: List<DayEvent>
)
