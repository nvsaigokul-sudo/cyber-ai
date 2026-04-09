package com.example.cyberai.service;

import com.example.cyberai.model.ChatResponse;
import com.example.cyberai.model.ThreatReport;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemediationService {

    public List<ChatResponse.RemediationStep> generateRemediationSteps(ThreatReport report) {
        List<ChatResponse.RemediationStep> steps = new ArrayList<>();

        if (report.getCritical() > 0) {
            steps.add(new ChatResponse.RemediationStep(
                    "IMMEDIATE: Isolate affected system",
                    "sudo iptables -A INPUT -j DROP",
                    "Linux"
            ));
            steps.add(new ChatResponse.RemediationStep(
                    "Kill suspicious processes",
                    "ps aux | grep -i suspicious | awk '{print $2}' | xargs sudo kill -9",
                    "Linux"
            ));
        }

        if (report.getHigh() > 0) {
            steps.add(new ChatResponse.RemediationStep(
                    "Run full antivirus scan",
                    "sudo clamscan --recursive --infected --remove /home",
                    "Linux"
            ));
        }

        if (report.getThreatScore() > 50) {
            steps.add(new ChatResponse.RemediationStep(
                    "Enable aggressive firewall rules",
                    "sudo ufw default deny incoming",
                    "Linux"
            ));
        }

        // Always include basic protection steps
        steps.add(new ChatResponse.RemediationStep(
                "Update security patches",
                "sudo apt update && sudo apt upgrade -y",
                "Linux"
        ));
        steps.add(new ChatResponse.RemediationStep(
                "Review authentication logs",
                "sudo cat /var/log/auth.log | grep -i failed | tail -20",
                "Linux"
        ));

        return steps;
    }

    public String getProtectionGuide() {
        return """
            🔒 COMPREHENSIVE PROTECTION GUIDE:
            
            1. Network Security:
               - Enable firewall: `sudo ufw enable`
               - Close unused ports: `sudo ufw deny [port]`
               - Monitor connections: `sudo netstat -tunap`
            
            2. System Hardening:
               - Update regularly: `sudo apt update && sudo apt upgrade`
               - Remove unnecessary services: `sudo systemctl disable [service]`
               - Use strong passwords
            
            3. Malware Prevention:
               - Install antivirus: `sudo apt install clamav`
               - Update definitions: `sudo freshclam`
               - Schedule regular scans
            
            4. Backup Strategy:
               - Daily backups: `rsync -av /source /backup`
               - Offsite backups recommended
            
            5. Monitoring:
               - Real-time log monitoring: `sudo journalctl -f`
               - Intrusion detection: `sudo apt install aide`
            """;
    }
}