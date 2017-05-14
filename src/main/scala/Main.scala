import java.io.File

import com.github.tototoshi.csv.CSVWriter
import net.ruippeixotog.scalascraper.browser.{ Browser, JsoupBrowser }
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

object Main extends App {
  val browser     = JsoupBrowser()

  login(browser)

  val f = new File("out.csv")
  val writer = CSVWriter.open(f)
  val header = List("メーカー", "名前", "容量", "シリーズ", "サロン価格", "価格", "割引率")
  writer.writeRow(header)

  val makers = getMakers(browser)

  makers.foreach { maker =>
    write(browser, writer, maker)
  }

  writer.close()


  // ------- functions -------
  def login(browser: Browser): Unit = {
    val loginForm = Map(
      "stat" -> "login",
      "id" -> "hplus333",
      "pw" -> "62423225"
    )
    browser.post("http://www.club-y.info/yindex.php", loginForm)
  }

  /**
   * メーカーの一覧の取得
   */
  def getMakers(browser: Browser): Seq[String] = {
    val url = "http://www.club-y.info/salon-norder1.php"
    val doc = browser.get(url)
    (doc >> texts("select[name=smakername] option")).drop(1).toSeq
  }

  /**
   * 渡されたurlの商品を渡されたwriterで書き込む。
   */
  def write(browser: Browser, writer: CSVWriter, maker: String, page: Int = 0): Unit = {
    val url       = s"http://www.club-y.info/salon-norder1.php?vmcode=3067&stat=search&sgoodsname=&smakername=$maker&sseriesname=&scat=&pagenum=$page&ordercode=&getcode=&idcode="
    val doc       = browser.get(url)
    val goodsList = (doc >> elementList("#HPB_TABLE_2_B_100212205608 tr")).drop(3).dropRight(3)

    if (goodsList.isEmpty) Unit
    else {
      goodsList.foreach { goods =>
        val tds = (goods >> texts("td")).toList
        val row = List(
          maker,  // メーカー
          tds(1), // 名前
          tds(3), // 容量
          tds(4), // シリーズ
          tds(5), // サロン価格
          tds(6), // 価格
          tds(7)  // 割引率
        )
        writer.writeRow(row)
      }
      Thread.sleep(500)
      write(browser, writer, maker, page + 1)
    }
  }
}

