package com.game.fingersinger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity {

	private String historyPath, audioPath, picturePath, tempPath;
	private String fileName = "";
	private String tempName = "";
	private Dialog onSaveDialog;

	private DrawLines drawView;
	private ScaleView tempoBar;
	private MediaRecorder mediaRecord;

	private ImageButton menuBtn, colorBtn, undoBtn;
	private ImageView pointer;
	private EditText edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设定调整音量为媒体音量,当暂停播放的时候调整音量就不会再默认调整铃声音量了

        this.setContentView(R.layout.loading);

        getDirPath();   
       	initView(); 

	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void initView() {
		
		setContentView(R.layout.activity_main);
		//LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout tempoBarLayout = (LinearLayout) findViewById(R.id.tempo_bar);
	    tempoBar = new ScaleView(this);
	    //tempoBar.setBackgroundColor(getResources().getColor(R.color.blue));
	    tempoBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
	    tempoBarLayout.addView(tempoBar);    
	 //   tempoBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_eraser));   

		LinearLayout drawLayout = (LinearLayout) findViewById(R.id.view_draw);
		//drawLayout.setLayoutParams(new LayoutParams())
		drawView = new DrawLines(this);
		drawView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));  
		drawLayout.addView(drawView);

		menuBtn = (ImageButton) findViewById(R.id.button_menu);
	    colorBtn = (ImageButton) findViewById(R.id.button_color);
		undoBtn = (ImageButton) findViewById(R.id.button_undo);
		pointer = (ImageView)findViewById(R.id.pointer);
		
		menuBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Menu");
	            if(Declare.menu_status == 1){
	            	menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_eraser));
	            	Declare.menu_status = 2;
					Toast.makeText(MainActivity.this, R.string.prompt_status_eraser, Toast.LENGTH_SHORT).show();
				}
				else if (Declare.menu_status == 2) {
					menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_voice));
					Declare.menu_status = 3;
					Toast.makeText(MainActivity.this, R.string.prompt_status_voice, Toast.LENGTH_SHORT).show();

					RelativeLayout voiceBarLayout = (RelativeLayout) findViewById(R.id.voice_bar);
				    voiceBarLayout.setVisibility(View.VISIBLE);
				    
				    changeVoice();
				}
				else {	//既可能是menu_status=3（音量）, 又可能menu_status=4（播放）
					RelativeLayout voiceBarLayout = (RelativeLayout) findViewById(R.id.voice_bar);
				    voiceBarLayout.setVisibility(View.GONE);
					menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_draw));
					Declare.menu_status = 1;
					undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_undo));
					Toast.makeText(MainActivity.this, R.string.prompt_status_draw, Toast.LENGTH_SHORT).show();
				}	
				
	       }

			private void changeVoice() {
				// TODO Auto-generated method stub
				SeekBar seek1 = (SeekBar)findViewById(R.id.seekBar1);
				SeekBar seek2 = (SeekBar)findViewById(R.id.seekBar2);
				SeekBar seek3 = (SeekBar)findViewById(R.id.seekBar3);
				SeekBar seek4 = (SeekBar)findViewById(R.id.seekBar4);
				SeekBar seek5 = (SeekBar)findViewById(R.id.seekBar5);
				
				seek1.setOnSeekBarChangeListener(new ProgressSeekListener(seek1, 0));
				seek2.setOnSeekBarChangeListener(new ProgressSeekListener(seek2, 1));
				seek3.setOnSeekBarChangeListener(new ProgressSeekListener(seek3, 2));
				seek4.setOnSeekBarChangeListener(new ProgressSeekListener(seek4, 3));
				seek5.setOnSeekBarChangeListener(new ProgressSeekListener(seek5, 4));
			}
	            
	    }); 
		
		colorBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Color");
	            if (Declare.color_status == 0) {
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_yellow));
					Declare.color_status = 1;
	            }
	            else if(Declare.color_status == 1){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_red));
					Declare.color_status = 2;
				}
	            else if (Declare.color_status == 2){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_blue));
					Declare.color_status = 3;
				}
	            else if (Declare.color_status == 3){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_purple));
					Declare.color_status = 4;
				}
				else {
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_green));
					Declare.color_status = 0;
				}	
	        }    
	    }); 
		
		undoBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Undo");
	            if (Declare.menu_status == 4) {
	            	//暂停
	            	undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_resume));
	            	Declare.menu_status = 5;
	            }
	            else if (Declare.menu_status == 5) {
	            	//播放
	            	undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
	            	Declare.menu_status = 4;
	            	playSong(Declare.pointerInMelody);
	            }
	            else {
	            	drawView.undo();	            	
	            }
	        }    
	    }); 

		pointer.setOnTouchListener(new PointerListener(this, pointer));
		
	} 

	private void getDirPath() {
		String dirPath;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			dirPath = Environment.getExternalStorageDirectory().getPath();
		}
		else 
			dirPath = Environment.getRootDirectory().getPath() + "/FingerSinger/history/";
		historyPath = dirPath + "/FingerSinger/history/";
		audioPath = dirPath + "/FingerSinger/output_audio/";
		picturePath = dirPath + "/FingerSinger/output_picture";
		
		File f1 = new File(this.historyPath); //strPath为路径
		if (!f1.exists()) {
			f1.mkdirs();	//建立文件夹 
		}
		
		File f2 = new File(this.audioPath); //strPath为路径
		if (!f2.exists()) {
			f2.mkdirs();	//建立文件夹 
		}
		
		File f3 = new File(this.picturePath); //strPath为路径
		if (!f3.exists()) {
			f3.mkdirs();	//建立文件夹
		}
	}	
	
	public boolean onOptionsItemSelected(MenuItem item) {  
	
		RelativeLayout voiceBarLayout = (RelativeLayout) findViewById(R.id.voice_bar);
	    voiceBarLayout.setVisibility(View.GONE);
	    
		// 在此说明一下，Menu相当于一个容器，而MenuItem相当于容器中容纳的东西 
		switch(item.getItemId()) { 
		case R.id.action_playCurrent: 
			item.setEnabled(false);
			menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_stop));
			undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
			Toast.makeText(MainActivity.this, R.string.prompt_status_eraser, Toast.LENGTH_SHORT).show();
			Declare.menu_status = 4;
			playSong(Declare.pointerInMelody);
			item.setEnabled(true);
			break; 
		case R.id.action_playWhole: 
			item.setEnabled(false);
			menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_stop));
			undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
        	Declare.menu_status = 4;
        	Log.v("playSong", "main,here");
        	playSong(0);
    		item.setEnabled(true);
			break; 
		case R.id.action_save: 
			if (Declare.isSaved == true) break;
			if (fileName == "") {
				//edit = (EditText)findViewById(R.id.name_editor);
				edit = new EditText(MainActivity.this);
				edit.setSingleLine();
				edit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE); 
			
				//提醒用户做保存的事情的一个框
				onSaveDialog = new AlertDialog.Builder(MainActivity.this).setTitle("保存")
					.setMessage("想一个靠谱的名字吧＝ ＝、").setView(edit)
					//设置按钮
					.setPositiveButton("保存", new DialogInterface.OnClickListener(){  
						public void onClick(DialogInterface dialog, int which) {
							fileName = edit.getText().toString();
							//Log.v("debuga", "fileName: " + fileName);
							
							if (new File(historyPath + "/" + fileName + ".psong").exists()) {
								Log.v("debuga", "file exists()");
								AlertDialog temp = new AlertDialog.Builder(MainActivity.this)
									.setTitle("保存失败").setMessage("有重名")
									.setPositiveButton("确定", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int which) {
											//不做任何操作
											edit.setVisibility(View.GONE);
										}
									}).create();
								temp.show();
							}
							else {
								//保存应该做的事
								try {
									onSave();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
//								
//								fileName = "";
//								//清空画布要做的事！！
//								Log.v("clearCanvas", "inside");
//								drawView.clearCanvas();
															
							}
						}  
					})
					.setCancelable(true).create();
				Log.v("clearCanvas", "outof");
				onSaveDialog.show();
			}

			else {
				//保存应该做的事
				try {
					onSave();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				fileName = "";
//				//清空画布要做的事！！
//				Log.v("clearCanvas", "else");
//				drawView.clearCanvas();
				
			}
			break; 
		case R.id.action_history:
			if (Declare.isSaved == false) {
				Dialog isSaveDialog = new AlertDialog.Builder(MainActivity.this).setMessage("当前曲目是否保存？")
					//设置按钮
					.setPositiveButton("保存", new DialogInterface.OnClickListener(){ 
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onSaveDialog.show();
							Declare.isSaved = true;
							//调文件系统，找出文件
							showFileList();
						}  
					})
					.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Declare.isSaved = true;
							//调文件系统，找出文件
							showFileList();
						}
					})
					.setCancelable(true).create();
				isSaveDialog.show();
			}
			else {
				//调文件系统，找出文件
				showFileList();
			}
			break; 
		case R.id.action_import: 
			Toast.makeText(MainActivity.this, "此功能尚未完成，敬请期待", Toast.LENGTH_SHORT).show();
			break; 
		case R.id.action_export:
			item.getSubMenu();
			break;
		case R.id.action_export_audio:
			if (fileName == "") {
				//edit = (EditText)findViewById(R.id.name_editor);
				edit = new EditText(MainActivity.this);
				edit.setSingleLine();
				edit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE); 
			
				//提醒用户做保存的事情的一个框
				Dialog onNameDialog = new AlertDialog.Builder(MainActivity.this).setTitle("得先保存")
					.setMessage("想一个靠谱的名字吧＝ ＝、").setView(edit)
					//设置按钮
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){  
						public void onClick(DialogInterface dialog, int which) {
							fileName = edit.getText().toString();
							//Log.v("debuga", "fileName: " + fileName);
							
							if (new File(historyPath + "/" + fileName + ".psong").exists()) {
								Log.v("debuga", "file exists()");
								AlertDialog temp = new AlertDialog.Builder(MainActivity.this)
									.setTitle("保存失败").setMessage("有重名")
									.setPositiveButton("确定", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int which) {
											//不做任何操作
											edit.setVisibility(View.GONE);
										}
									}).create();
								temp.show();
							}
							else {
								//保存应该做的事
								try {
									onSave();
									recordSong();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								fileName = "";
								//清空画布要做的事！！
								drawView.clearCanvas();
															
							}
						}  
					})
					.setCancelable(true).create();
				onNameDialog.show();
			}

			break;
		case R.id.action_export_picture:
			Toast.makeText(MainActivity.this, "此功能尚未完成，敬请期待", Toast.LENGTH_SHORT).show();
			break;
		default:
			Log.v("debuga", "menu_id: " + item.getItemId());
			break;
		} 
		return true;  
	}  

	
	private void recordSong() {
		mediaRecord = new MediaRecorder();
		//mediaRecord.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);	//设置音频源
		mediaRecord.setAudioSource(MediaRecorder.AudioSource.DEFAULT);	//设置音频源
		mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);	//设置音频输出格式
		mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);	//设置音频编码
		
		mediaRecord.setOutputFile(audioPath + "/" + fileName + ".mp3");
		try {
			mediaRecord.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaRecord.start();
		this.playSong(0);
		mediaRecord.stop();
		mediaRecord.release();
	}

	
	private void loadFromHistory() {		
		try{
			//从文件中恢复
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(historyPath + "/" + fileName + ".psong"));
			Declare.melody = (Melody[])in.readObject();
			in.close();
			
			//重画画布
			drawView.clearCanvas();
			drawView.reDraw();
			
		}
		catch(Exception e){
	       e.printStackTrace();
        }
		
	}

	@SuppressLint("SimpleDateFormat")
	private void showFileList() {
		ListView listview = (ListView)this.findViewById(R.id.list_view);
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		listview.setBackgroundColor(Color.GRAY);
		File f = new File(this.historyPath); //strPath为路径
		//File f = new File(Environment.getRootDirectory().getPath()); //strPath为路径

		//Log.v("debuga", f.getPath());
		if (!(f.exists())) {
			Log.v("debuga", "not exists");
			Dialog dialog = new AlertDialog.Builder(MainActivity.this)  
				//设置对话框要显示的消息  
				.setMessage("无历史曲目")  
				//设置按钮
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){  
					public void onClick(DialogInterface dialog, int which) {  
						//好像什么都不用干
					}  
				}).create();
			dialog.show();
		}
		else {
			String[] file_list = f.list(); //String[] 
			if (file_list.length == 0) {
				Dialog dialog = new AlertDialog.Builder(MainActivity.this)  
					//设置对话框要显示的消息  
					.setMessage("无历史曲目")  
					//设置按钮
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){  
						public void onClick(DialogInterface dialog, int which) {  
							//好像什么都不用干
						}  
					}).create();
				dialog.show();
			}
			else {
				SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
				Log.v("debuga", "file_list:" + f.exists());
				for (int i = 0; i < file_list.length; i++) {
					if (file_list[i].endsWith(".psong")) {
						HashMap<String, String> info = new HashMap<String, String>();
						Log.v("debuga", "aaa:" + dateformat1.format(((new File(historyPath + "/" + file_list[i])).lastModified())) + " length:" + file_list[i]);
						info.put("name", file_list[i].substring(0, file_list[i].length() - 6));
						info.put("time", dateformat1.format(((new File(historyPath + "/" + file_list[i])).lastModified())));
						data.add(info);
					}
				}
			}
		}
		
		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.item_menu, 
				new String[] {"name", "time"}, new int[] {R.id.name, R.id.time});
		listview.setAdapter(adapter);
		listview.bringToFront();
		listview.setVisibility(View.VISIBLE);
		Toast.makeText(MainActivity.this, "LongClick on item to delete", Toast.LENGTH_SHORT).show();
		
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView lv = (ListView)arg0;
				lv.setVisibility(View.GONE);
				@SuppressWarnings("unchecked")
				HashMap<String, String> tem = (HashMap<String, String>)lv.getItemAtPosition(arg2);
				tempName = tem.get("name");
				Dialog isDeleteDialog = new AlertDialog.Builder(MainActivity.this).setMessage("是否删除本曲目？")
						//设置按钮
						.setPositiveButton("删了吧", new DialogInterface.OnClickListener(){ 
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								File f = new File(historyPath + "/" + tempName + ".psong");
								f.delete();
							}  
						})
						.setNegativeButton("不要删", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//好像什么都不用做
							}
						})
						.setCancelable(true).create();
					isDeleteDialog.show();
				return false;
			}
		});
	
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView lv = (ListView)arg0;
				@SuppressWarnings("unchecked")
				HashMap<String, String> tem = (HashMap<String, String>)lv.getItemAtPosition(arg2);
				fileName = tem.get("name");
				//Toast.makeText(MainActivity.this, "Click" + fileName, 200).show();
				loadFromHistory();
				lv.setVisibility(View.GONE);
			}
		});
	}

	private void onSave() throws IOException {
		File f = new File(this.historyPath); //strPath为路径
		if (!f.exists()) {
			f.mkdirs();	//建立文件夹 
			Log.v("debug", "mkdir~");
			Log.v("debug", "f.exists(): " + f.getPath() + " " + f.exists());
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(historyPath + "/" + fileName + ".psong"));
		out.writeObject(Declare.melody);
		out.close();
		Declare.isSaved = true;
		Toast.makeText(MainActivity.this, "Already Saved", Toast.LENGTH_SHORT).show();
	}
	
	public void playSong(int start) {
	
//		if (start == 0) {
//			start = 10000;
//			Log.v("pstart", "here");
//			for (int i = 0; i < 5; i++) {
//				if (Declare.melody[i].starts.size() == 0) continue;
//				int temp = Declare.melody[i].starts.get(0);
//				Log.v("pstart", "start/temp: " + start + "/" + temp);
//				if (temp < start) {
//					start = temp;
//				}
//			}
//			if (start == 10000) start = 0;
//		}
		int end = 0;
		for (int i = 0; i < 5; i++) {
			Log.v("playSong", "color: " + i + " size:" + Declare.melody[i].stops.size());
			if (Declare.melody[i].stops.size() == 0) continue;
			int temp = Declare.melody[i].stops.get(Declare.melody[i].stops.size() - 1) - 1;
			if (temp > end) {
				end = temp;
			}
		}
		Log.v("playSong", "before for loop：" + start + "/" + end);
		for (int i = start; i < end; i++) {
			for (int j = 0; j < 5; j++) {
				if (Declare.melody[j].notes.isEmpty()) continue;
				if (Declare.melody[j].stops.get(Declare.melody[j].stops.size() - 1) - 1 < i) continue;
				int note = Declare.melody[j].notes.get(i);
				Log.v("playSong", start + "/" + end + " note: " + note);
				if (Declare.menu_status == 5) {
					break;
				}
				if (note == 0) {
					Declare.soundManager[j].playSound(0, Declare.melody[j].voice);
				}
				else {
					Declare.soundManager[j].playSound(Declare.getIndexOfSound(note) + 22 * j, Declare.melody[j].voice);
				}
			}	
			if (Declare.menu_status == 5) {
				break;
			}
			try {
				Thread.sleep((long) (500 * Declare.speed));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_draw));
		undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_undo));
		Declare.menu_status = 1;
		Toast.makeText(MainActivity.this, R.string.prompt_stop_play, Toast.LENGTH_SHORT).show();
		
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		drawView.reDraw();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
}
