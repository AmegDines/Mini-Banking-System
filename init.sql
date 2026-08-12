-- Initialize transaction_summary table
CREATE TABLE IF NOT EXISTS transaction_summary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_transferred DECIMAL(15, 2) NOT NULL DEFAULT 0.00
);

-- Insert the initial row if the table is empty
INSERT INTO transaction_summary (total_transferred)
SELECT 0.00 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM transaction_summary);

-- Create trigger to automatically update total_transferred
DELIMITER //

CREATE TRIGGER after_transaction_insert
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    UPDATE transaction_summary
    SET total_transferred = total_transferred + NEW.amount
    WHERE id = 1;
END;
//

DELIMITER ;
