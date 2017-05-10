package consumer

import java.util.Properties

import domain.User
import kafka.consumer.{Consumer, ConsumerConfig, ConsumerTimeoutException, Whitelist}
import kafka.serializer.DefaultDecoder
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader

import scala.io.Source

/**
  * Created by Israel Gaytan on 10/05/17.
  */
class KafkaConsumer {

  private val props = new Properties()

  val groupId = "demo-topic-consumer"

  val topic = "test"

  //Connect to zookeeper
  props.put("group.id", groupId)
  props.put("zookeeper.connect", "localhost:2181")
  props.put("auto.offset.reset", "smallest")
  props.put("consumer.timeout.ms", "120000")
  props.put("auto.commit.interval.ms", "10000")

  private val consumerConfig = new ConsumerConfig(props)

  private val consumerConnector = Consumer.create(consumerConfig)

  private val filterSpec = new Whitelist(topic)

  private val streams = consumerConnector.createMessageStreamsByFilter(filterSpec, 1, new DefaultDecoder(), new DefaultDecoder())(0)

  lazy val iterator = streams.iterator()

  //read avro schema file

  val schemaString = Source.fromURL(getClass.getResource("/schema.avsc")).mkString

  // Initialize schema

  val schema: Schema = new Schema.Parser().parse(schemaString)

  def read() =

    try {

      if (hasNext) {

        println("Getting message from queue.............")

        val message: Array[Byte] = iterator.next().message()

        getUser(message)

      } else {

        None

      }

    } catch {

      case ex: Exception => ex.printStackTrace()

        None

    }

  private def hasNext: Boolean =

    try

      iterator.hasNext()

    catch {

      case timeOutEx: ConsumerTimeoutException =>

        false

      case ex: Exception => ex.printStackTrace()

        println("Got error when reading message ")

        false

    }

  private def getUser(message: Array[Byte]) = {

    // Deserialize and create generic record

    val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)

    val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)

    val userData: GenericRecord = reader.read(null, decoder)

    // Make user object

    val user = User(userData.get("id").toString.toInt, userData.get("name").toString, try {

      Some(userData.get("email").toString)

    } catch {

      case _ => None

    })

    Some(user)

  }

}

