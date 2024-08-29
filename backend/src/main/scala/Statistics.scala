import org.apache.spark.sql.functions.{avg, col, max, pow, sqrt, stddev}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Encoders

object Statistics {

  def abnormalPeaks(df: DataFrame): Double = {
    // Compute average ECG
    val avgECG = df.agg(avg("ecg_1")).first().getDouble(0)
    // Standard deviation can only be computed from two tuples, so at the beginning it's still 0
    var stdDev: Double = 0
    if (df.count() > 1)
      stdDev = df.agg(stddev("ecg_1")).first().getDouble(0)
    val threshold = avgECG + (3 * stdDev) // Threshold beyond which there are abnormal peaks in the ECG
    val abnormalPeaks: Double = df.filter(col("ecg_1") > threshold).count()
    val percAbnormalPeaks = (abnormalPeaks / df.count()) * 100
    return percAbnormalPeaks
  }

  def cardiacFrequency(df: DataFrame, subject: Int, activity: Int): Double = {
    // Take the maximum ECG of this activity
    val max_ecg_activity = Main.generalHistory.max_ecg(subject - 1)(activity - 1)
    // Count the number of R waves and divide it for the sampling time
    val numRwaves = df.filter(col("ecg_1") > Utils.bpm_threshold * max_ecg_activity).count()
    val sampling_time = df.count() * Utils.sampling_frequency // Milliseconds
    val numRwavesPerMs = numRwaves.toDouble / sampling_time.toDouble
    // Multiply by 60 seconds
    val cardiacFrequency = numRwavesPerMs * 60000
    return Math.round(cardiacFrequency)
  }

  // Since each batch duration is about 3 seconds, treat the average acceleration within the batch data as a sort
  // of instantaneous acceleration, then compute the magnitude and store it in the statistics
  def batch_accelerations(df: DataFrame): String = {
    // Chest accelerations
    val acc_chest_x = df.agg(avg("acceleration_chest_X")).first().getDouble(0)
    val acc_chest_y = df.agg(avg("acceleration_chest_Y")).first().getDouble(0)
    val acc_chest_z = df.agg(avg("acceleration_chest_Z")).first().getDouble(0)
    val acc_chest = Math.sqrt(acc_chest_x * acc_chest_x + acc_chest_y * acc_chest_y + acc_chest_z * acc_chest_z)
    // Ankle accelerations
    val acc_ankle_x = df.agg(avg("acceleration_l_ankle_X")).first().getDouble(0)
    val acc_ankle_y = df.agg(avg("acceleration_l_ankle_Y")).first().getDouble(0)
    val acc_ankle_z = df.agg(avg("acceleration_l_ankle_Z")).first().getDouble(0)
    val acc_ankle = Math.sqrt(acc_ankle_x * acc_ankle_x + acc_ankle_y * acc_ankle_y + acc_ankle_z * acc_ankle_z)
    // Arm accelerations
    val acc_arm_x = df.agg(avg("acceleration_r_lower_arm_X")).first().getDouble(0)
    val acc_arm_y = df.agg(avg("acceleration_r_lower_arm_Y")).first().getDouble(0)
    val acc_arm_z = df.agg(avg("acceleration_r_lower_arm_Z")).first().getDouble(0)
    val acc_arm = Math.sqrt(acc_arm_x * acc_arm_x + acc_arm_y * acc_arm_y + acc_arm_z * acc_arm_z)
    return acc_chest + ";" + acc_ankle + ";" + acc_arm
  }

  def maxAccelerationChest(df: DataFrame): String = {
    return df.withColumn("magnitude", sqrt(pow(col("acceleration_chest_X"), 2) + pow(col("acceleration_chest_Y"), 2) + pow(col("acceleration_chest_Z"), 2)))
      .agg(max("magnitude")).first().getDouble(0).toString
  }

  def createSessionHistoryRecord(df: DataFrame): String = {
    var activity = 0d
    var percAbnormalPeaks = 0d
    var avg_cardiacFrequency = 0d
    var max_acceleration_chest = 0d
    var subject = 0
    var dynamic = false
    var ratio_correct_pred_activity = 0d
    try {
      activity = df.tail(1)(0).getDouble(5)
      percAbnormalPeaks = df.tail(1)(0).getDouble(0)
      avg_cardiacFrequency = df.agg(avg("cardiacFrequency")).first().getDouble(0)
      max_acceleration_chest = df.agg(max("acceleration_chest")).first().getDouble(0)
      subject = df.first().getInt(11)
      dynamic = Utils.dynamic_activities.contains(activity.toInt)
      ratio_correct_pred_activity = (df.filter(col("activity") === col("pred_activity")).count().toDouble / df.count().toDouble) * 100
    } catch {
      case e: NullPointerException => {}
    }
    return subject + "," + activity + "," + dynamic + "," + ratio_correct_pred_activity + "," + percAbnormalPeaks + "," + avg_cardiacFrequency + "," + max_acceleration_chest
  }

  def ratioCorrectPredActivity(df: DataFrame, activityClassifier: ActivityClassifier): Double = {
    val pred_activity = activityClassifier.predict(df)
    val ratio_correct_pred_activity = (df.filter(col("activity") === pred_activity).count().toDouble / df.count().toDouble) * 100
    return ratio_correct_pred_activity
  }

  /**
   * Generic query made on 'infoSubjects' and 'infoActivities' dataframes that returns a string containing the
   * desired statistic for every subject or activity (respectively)
   */
  def generic_query(df: DataFrame, statistic: String): String = {
    if (df.isEmpty)
      return ""
    return df.select(col(statistic)).as(Encoders.STRING).collect().toSeq.mkString(",")
  }

  def num_dynamic_and_static_activities(df: DataFrame, subject: Int): String = {
    if (subject.equals(-1)) {
      val num_dynamic_activities = df.filter(col("dynamic") === true).count().toInt
      val num_static_activities = df.count().toInt - num_dynamic_activities
      return num_dynamic_activities + "," + num_static_activities
    }
    else {
      val num_dynamic_activities = df.filter(col("dynamic") === true).filter(col("subject") === subject.toDouble).count().toInt
      val num_static_activities = df.filter(col("subject") === subject.toDouble).count().toInt - num_dynamic_activities
      return num_dynamic_activities + "," + num_static_activities
    }
  }

  def stddev_avg_bpm(df: DataFrame): String = {
    if (!df.isEmpty) {
      val subject: Double = df.first().getDouble(0)
      val df_subject: DataFrame = Main.generalHistory.df.filter(col("subject") === subject)
      val all_activities_subject: DataFrame = df_subject.union(df)
      return all_activities_subject.agg(stddev("avg_cardiacFrequency")).first().getDouble(0).toString
    }
    return "";
  }

}
