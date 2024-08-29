import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{BooleanType, DoubleType, IntegerType, StringType, StructType}

object Utils {

  val numSubjects = 5
  val numActivities = 11

  val batch_interval = 3000 // Interval (in milliseconds) between data batches
  val sampling_frequency = 20 // Every 20 milliseconds a measurement on the sensors is made

  val path_training_data = "~/projects/motion_insights/training_data"
  val path_realtime_data = "~/projects/motion_insights/realtime_data"

  val path_general_data_history = "~/projects/motion_insights/history_statistics/general_data_history"
  val path_info_subjects = "~/projects/motion_insights/history_statistics/infoSubjects"
  val path_info_activities = "~/projects/motion_insights/history_statistics/infoActivities"
  val path_max_ecg = "~/projects/motion_insights/history_statistics/max_ecg.txt"

  val bpm_threshold = 0.525 // Threshold beyond which an R wave is identified while computing BPMs

  val data_schema = new StructType()
    .add("acceleration_chest_X", DoubleType) //0)
    .add("acceleration_chest_Y", DoubleType) //1)
    .add("acceleration_chest_Z", DoubleType) //2)
    .add("ecg_1", DoubleType) //3)
    .add("ecg_2", DoubleType) //non utilizzato //4)
    .add("acceleration_l_ankle_X", DoubleType) //5)
    .add("acceleration_l_ankle_Y", DoubleType) //6)
    .add("acceleration_l_ankle_Z", DoubleType) //7)
    .add("gyro_l_ankle_X", DoubleType) //8)
    .add("gyro_l_ankle_Y", DoubleType) //9)
    .add("gyro_l_ankle_Z", DoubleType) //10)
    .add("magnetometer_l_ankle_X", DoubleType) //11)
    .add("magnetometer_l_ankle_Y", DoubleType) //12)
    .add("magnetometer_l_ankle_Z", DoubleType) //13)
    .add("acceleration_r_lower_arm_X", DoubleType) //14)
    .add("acceleration_r_lower_arm_Y", DoubleType) //15)
    .add("acceleration_r_lower_arm_Z", DoubleType) //16)
    .add("gyro_r_lower_arm_X", DoubleType) //17)
    .add("gyro_r_lower_arm_Y", DoubleType) //18)
    .add("gyro_r_lower_arm_Z", DoubleType) //19)
    .add("magnetometer_r_lower_arm_X", DoubleType) //20)
    .add("magnetometer_r_lower_arm_Y", DoubleType) //21)
    .add("magnetometer_r_lower_arm_Z", DoubleType) //22)
    .add("activity", DoubleType) //23)

  val statistics_schema = new StructType()
    .add("percAbnormalPeaks", DoubleType) //0)
    .add("cardiacFrequency", DoubleType) //1)
    .add("acceleration_chest", DoubleType) //2)
    .add("acceleration_ankle", DoubleType) //3)
    .add("acceleration_arm", DoubleType) //4)
    .add("activity", DoubleType) //5)
    .add("pred_activity", DoubleType) //6)
    .add("predictions", StringType) //7)
    .add("activity_description", StringType) //8)
    .add("last_tuple", StringType) //9)
    .add("last_ecg_column", StringType) //10)
    .add("subject", IntegerType) //11)

  val session_history_schema = new StructType()
    .add("subject", DoubleType) //0)
    .add("activity", DoubleType) //1)
    .add("dynamic", BooleanType) //2)
    .add("ratio_correct_pred_activity", DoubleType) //3)
    .add("percAbnormalPeaks", DoubleType) //4)
    .add("avg_cardiacFrequency", DoubleType) //5)
    .add("max_acceleration_chest", DoubleType) //6)

  val info_subjects_schema = new StructType()
    .add("subject", DoubleType) //0)
    .add("num_activities", IntegerType) //1)
    .add("max_acceleration_chest", DoubleType) //2)
    .add("max_perc_abnormal_peaks", DoubleType) //3)

  val info_activities_schema = new StructType()
    .add("activity", DoubleType) //0)
    .add("num_performed", IntegerType) //1)
    .add("worst_prediction", DoubleType) //2)

  val endOfActivity = "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0"

  val dynamic_activities = Set(4, 5, 9, 10, 11)

  val activity_description = Map[Int, String]((1, "Standing still"), (2, "Sitting and relaxing"), (3, "Lying down"),
    (4, "Walking"), (5, "Climbing stairs"), (6, "Waist bends forward"), (7, "Frontal elevation of arms"),
    (8, "Knees bending (crouching)"), (9, "Cycling"), (10, "Jogging"), (11, "Running"))

  def createRow(line: Array[String], schema: StructType): Row = {
    var i = 0
    val rowValues = for (field <- schema.fields) yield {
      val value = field.dataType match {
        case StringType => line(i)
        case IntegerType => line(i).toInt
        case DoubleType => line(i).toDouble
        case BooleanType => line(i).toBoolean
        case _ => throw new Exception("Invalid data type")
      }
      i += 1
      value
    }
    return Row.fromSeq(rowValues)
  }

}
